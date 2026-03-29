package com.hhplus.ecommerce.payment.usecase

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import com.hhplus.ecommerce.payment.usecase.dto.PaymentInfo
import com.hhplus.ecommerce.product.domain.StockReservationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 결제 처리 Facade
 *
 * ## 트랜잭션 전략
 * balanceService.use()는 @RedisLock 내부에서 즉시 커밋되므로,
 * 이후 단계 실패 시 단일 트랜잭션 롤백이 불가능하다.
 * 따라서 보상 트랜잭션(Compensation) 패턴을 적용한다.
 *
 * ## 단계별 실패 처리
 * 1. balanceService.use() 실패  → 잔액 차감 없음, 예외 전파
 * 2. paymentService.pay() 실패  → 잔액 차감됨 → [보상] balanceService.charge()로 환불
 * 3. orderComplete() 실패       → 잔액 차감 + 결제 생성됨 → [보상] balanceService.charge()로 환불
 *                                  (환불 실패 시 수동 처리 필요 — 에러 로그로 식별)
 */
@Component
class PaymentFacade(
    private val balanceService: BalanceService,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val stockReservationService: StockReservationService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentFacade::class.java)
    }

    fun pay(dto: PaymentCreation): PaymentInfo {
        // 재고 확보가 완료된 주문만 결제 가능 (STOCK_CONFIRMED)
        // REQUESTED 상태에서의 결제 시도는 OrderNotFoundException으로 거부됨
        val orderQuery = OrderQuery(
            orderId = dto.orderId,
            status = OrderStatus.STOCK_CONFIRMED
        )
        val order = orderService.getOrder(orderQuery)

        val useCommand = BalanceTransaction(
            userId = dto.userId,
            amount = order.totalPrice,  // totalPrice = sum(수량 × 단가)
            type = BalanceTransaction.TransactionType.USE
        )
        val refundCommand = BalanceTransaction(
            userId = dto.userId,
            amount = order.totalPrice,
            type = BalanceTransaction.TransactionType.CHARGE
        )

        // 1단계: 잔액 차감 — @RedisLock 내부에서 즉시 커밋
        balanceService.use(useCommand)

        // 2~3단계: 결제 생성 + 주문 확정 — 실패 시 잔액 환불(보상)
        return try {
            val result = paymentService.pay(
                CreationPaymentCommand(
                    orderId = dto.orderId,
                    userId = dto.userId,
                    price = order.totalPrice,
                )
            )
            orderService.orderComplete(OrderCompleteCommand(dto.orderId))
            // 결제 완료 후 예약 재고 확정 차감 (soft reserve → commit)
            stockReservationService.commit(dto.orderId)
            PaymentInfo.from(result)

        } catch (e: Exception) {
            logger.error(
                "결제 처리 실패 — 잔액 환불 처리 시작: userId=${dto.userId}, orderId=${dto.orderId}, amount=${order.totalPrice}",
                e
            )
            runCatching { balanceService.charge(refundCommand) }
                .onSuccess { logger.info("잔액 환불 완료: userId=${dto.userId}, amount=${order.totalPrice}") }
                .onFailure { refundEx ->
                    logger.error(
                        "[수동 처리 필요] 잔액 환불 실패: userId=${dto.userId}, orderId=${dto.orderId}, amount=${order.totalPrice}",
                        refundEx
                    )
                }
            throw e
        }
    }
}
