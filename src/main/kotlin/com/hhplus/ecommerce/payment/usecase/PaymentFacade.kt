package com.hhplus.ecommerce.payment.usecase

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.repository.IPaymentSagaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaStatus
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import com.hhplus.ecommerce.payment.usecase.dto.PaymentInfo
import com.hhplus.ecommerce.product.domain.StockReservationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 결제 처리 Facade (PaymentSaga 상태 추적 포함)
 *
 * ## 트랜잭션 전략
 * balanceService.use()는 @RedisLock 내부에서 즉시 커밋되므로,
 * 이후 단계 실패 시 단일 트랜잭션 롤백이 불가능하다.
 * 따라서 보상 트랜잭션(Compensation) 패턴을 적용한다.
 *
 * ## Saga 상태 전이
 * STARTED → BALANCE_DEDUCTED → PAYMENT_CREATED → ORDER_CONFIRMED → STOCK_COMMITTED → COMPLETED
 *                                                                                        ↑ 성공
 * STARTED → BALANCE_DEDUCTED → (실패) → COMPENSATING → FAILED          (보상 성공)
 *                                                     → COMPENSATION_FAILED  (보상 실패 — 수동 처리 필요)
 *
 * ## 단계별 실패 처리
 * 1. balanceService.use() 실패    → FAILED, 잔액 차감 없음
 * 2. paymentService.pay() 실패   → COMPENSATING → 환불 → FAILED / COMPENSATION_FAILED
 * 3. orderComplete() 실패        → COMPENSATING → 환불 → FAILED / COMPENSATION_FAILED
 */
@Component
class PaymentFacade(
    private val balanceService: BalanceService,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val stockReservationService: StockReservationService,
    private val paymentSagaRepository: IPaymentSagaRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentFacade::class.java)
    }

    fun pay(dto: PaymentCreation): PaymentInfo {
        // Saga 시작 — STARTED 상태 저장
        val saga = paymentSagaRepository.save(
            PaymentSagaEntity(orderId = dto.orderId, userId = dto.userId)
        )

        // 재고 확보가 완료된 주문만 결제 가능 (STOCK_CONFIRMED)
        val orderQuery = OrderQuery(orderId = dto.orderId, status = OrderStatus.STOCK_CONFIRMED)
        val order = orderService.getOrder(orderQuery)

        val useCommand = BalanceTransaction(
            userId = dto.userId,
            amount = order.totalPrice,
            type = BalanceTransaction.TransactionType.USE
        )
        val refundCommand = BalanceTransaction(
            userId = dto.userId,
            amount = order.totalPrice,
            type = BalanceTransaction.TransactionType.CHARGE
        )

        // 1단계: 잔액 차감 — @RedisLock 내부에서 즉시 커밋
        try {
            balanceService.use(useCommand)
            saga.transition(PaymentSagaStatus.BALANCE_DEDUCTED)
            paymentSagaRepository.save(saga)
        } catch (e: Exception) {
            saga.transition(PaymentSagaStatus.FAILED, e.message)
            paymentSagaRepository.save(saga)
            logger.error("SAGA:BALANCE_DEDUCT:FAILED orderId=${dto.orderId}", e)
            throw e
        }

        // 2~4단계: 결제 생성 + 주문 확정 + 재고 커밋 — 실패 시 보상 트랜잭션
        return try {
            // 2단계: 결제 엔티티 생성
            val result = paymentService.pay(
                CreationPaymentCommand(
                    orderId = dto.orderId,
                    userId = dto.userId,
                    price = order.totalPrice,
                )
            )
            saga.paymentId = result.paymentId
            saga.transition(PaymentSagaStatus.PAYMENT_CREATED)
            paymentSagaRepository.save(saga)

            // 3단계: 주문 확정
            orderService.orderComplete(OrderCompleteCommand(dto.orderId))
            saga.transition(PaymentSagaStatus.ORDER_CONFIRMED)
            paymentSagaRepository.save(saga)

            // 4단계: 예약 재고 확정 차감 (soft reserve → commit)
            stockReservationService.commit(dto.orderId)
            saga.transition(PaymentSagaStatus.STOCK_COMMITTED)
            paymentSagaRepository.save(saga)

            // 완료
            saga.transition(PaymentSagaStatus.COMPLETED)
            paymentSagaRepository.save(saga)
            logger.info("SAGA:COMPLETED orderId=${dto.orderId}")

            PaymentInfo.from(result)

        } catch (e: Exception) {
            logger.error("SAGA:PAYMENT_FLOW:FAILED orderId=${dto.orderId}, sagaStatus=${saga.sagaStatus}", e)

            // 보상 트랜잭션 시작
            saga.transition(PaymentSagaStatus.COMPENSATING, e.message)
            paymentSagaRepository.save(saga)

            runCatching { balanceService.charge(refundCommand) }
                .onSuccess {
                    saga.transition(PaymentSagaStatus.FAILED)
                    paymentSagaRepository.save(saga)
                    logger.info("SAGA:COMPENSATION:SUCCESS orderId=${dto.orderId}, 잔액 환불 완료")
                }
                .onFailure { refundEx ->
                    saga.transition(PaymentSagaStatus.COMPENSATION_FAILED, refundEx.message)
                    paymentSagaRepository.save(saga)
                    logger.error(
                        "[수동 처리 필요] SAGA:COMPENSATION:FAILED orderId=${dto.orderId}, userId=${dto.userId}, amount=${order.totalPrice}",
                        refundEx
                    )
                }

            throw e
        }
    }
}
