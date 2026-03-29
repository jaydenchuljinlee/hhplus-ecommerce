package com.hhplus.ecommerce.payment.usecase

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.notification.domain.event.INotificationEventPublisher
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.payment.domain.ExternalApiService
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.dto.ExternalCallRequest
import com.hhplus.ecommerce.payment.domain.repository.IPaymentSagaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaStatus
import com.hhplus.ecommerce.payment.usecase.dto.PaymentBreakdown
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import com.hhplus.ecommerce.payment.usecase.dto.PaymentInfo
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.user.domain.UserService
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
    private val paymentSagaRepository: IPaymentSagaRepository,
    private val notificationEventPublisher: INotificationEventPublisher,
    private val userService: UserService,
    private val externalApiService: ExternalApiService
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

        // breakdown 결정: null이면 기존 잔액 단독 결제 (하위 호환)
        val breakdown = dto.breakdown ?: PaymentBreakdown.balanceOnly(order.totalPrice)

        // 실제 결제 금액 검증 (쿠폰 할인 반영)
        val actualPayAmount = breakdown.totalAmount - breakdown.couponDiscount
        require(actualPayAmount == order.totalPrice) {
            "결제 금액(${actualPayAmount})이 주문 금액(${order.totalPrice})과 일치하지 않습니다."
        }

        val payMethod = breakdown.payMethod

        // 잔액 차감 여부 추적 (보상 트랜잭션 분기용)
        var balanceDeducted = false

        // 1단계: 잔액 차감 — balanceAmount > 0인 경우만 실행 (@RedisLock 내부에서 즉시 커밋)
        if (breakdown.balanceAmount > 0) {
            val useCommand = BalanceTransaction(
                userId = dto.userId,
                amount = breakdown.balanceAmount,
                type = BalanceTransaction.TransactionType.USE
            )
            try {
                balanceService.use(useCommand)
                balanceDeducted = true
                saga.transition(PaymentSagaStatus.BALANCE_DEDUCTED)
                paymentSagaRepository.save(saga)
            } catch (e: Exception) {
                saga.transition(PaymentSagaStatus.FAILED, e.message)
                paymentSagaRepository.save(saga)
                logger.error("SAGA:BALANCE_DEDUCT:FAILED orderId=${dto.orderId}", e)
                throw e
            }
        }

        // 2~5단계: 카드 결제 + 결제 엔티티 생성 + 주문 확정 + 재고 커밋 — 실패 시 보상 트랜잭션
        return try {
            // 2단계: 카드 결제 (cardAmount > 0인 경우 — 외부 PG 연동)
            if (breakdown.cardAmount > 0) {
                externalApiService.call(
                    ExternalCallRequest(
                        payId = dto.orderId,
                        userId = dto.userId,
                        price = breakdown.cardAmount
                    )
                )
            }

            // 3단계: 결제 엔티티 생성
            val result = paymentService.pay(
                CreationPaymentCommand(
                    orderId = dto.orderId,
                    userId = dto.userId,
                    price = actualPayAmount,
                    payMethod = payMethod
                )
            )
            saga.paymentId = result.paymentId
            saga.transition(PaymentSagaStatus.PAYMENT_CREATED)
            paymentSagaRepository.save(saga)

            // 4단계: 주문 확정
            orderService.orderComplete(OrderCompleteCommand(dto.orderId))
            saga.transition(PaymentSagaStatus.ORDER_CONFIRMED)
            paymentSagaRepository.save(saga)

            // 5단계: 예약 재고 확정 차감 (soft reserve → commit)
            stockReservationService.commit(dto.orderId)
            saga.transition(PaymentSagaStatus.STOCK_COMMITTED)
            paymentSagaRepository.save(saga)

            // 완료
            saga.transition(PaymentSagaStatus.COMPLETED)
            paymentSagaRepository.save(saga)
            logger.info("SAGA:COMPLETED orderId=${dto.orderId} payMethod=${payMethod}")

            // 누적 구매 금액 반영 + 등급 재산정 (비필수 — 실패해도 결제 결과에 영향 없음)
            runCatching {
                userService.addPurchaseAmount(dto.userId, order.totalPrice)
            }.onFailure { e ->
                logger.warn("USER:GRADE_UPDATE 실패 (비필수) userId=${dto.userId}", e)
            }

            // 결제 완료 알림 발행 (비필수 — 실패해도 결제 결과에 영향 없음)
            runCatching {
                notificationEventPublisher.publish(
                    NotificationEvent.paymentConfirmed(
                        userId = dto.userId,
                        orderId = dto.orderId,
                        amount = order.totalPrice
                    )
                )
            }.onFailure { e ->
                logger.warn("PAYMENT_CONFIRMED 알림 발행 실패 (비필수) orderId=${dto.orderId}", e)
            }

            PaymentInfo.from(result)

        } catch (e: Exception) {
            logger.error("SAGA:PAYMENT_FLOW:FAILED orderId=${dto.orderId}, sagaStatus=${saga.sagaStatus}", e)

            // 보상 트랜잭션 시작
            saga.transition(PaymentSagaStatus.COMPENSATING, e.message)
            paymentSagaRepository.save(saga)

            if (balanceDeducted) {
                // 잔액 차감이 발생한 경우에만 환불
                val refundCommand = BalanceTransaction(
                    userId = dto.userId,
                    amount = breakdown.balanceAmount,
                    type = BalanceTransaction.TransactionType.CHARGE
                )
                runCatching { balanceService.charge(refundCommand) }
                    .onSuccess {
                        saga.transition(PaymentSagaStatus.FAILED)
                        paymentSagaRepository.save(saga)
                        logger.info("SAGA:COMPENSATION:SUCCESS orderId=${dto.orderId}, 잔액 환불 완료 amount=${breakdown.balanceAmount}")
                    }
                    .onFailure { refundEx ->
                        saga.transition(PaymentSagaStatus.COMPENSATION_FAILED, refundEx.message)
                        paymentSagaRepository.save(saga)
                        logger.error(
                            "[수동 처리 필요] SAGA:COMPENSATION:FAILED orderId=${dto.orderId}, userId=${dto.userId}, amount=${breakdown.balanceAmount}",
                            refundEx
                        )
                    }
            } else {
                // 잔액 차감 없음 — 카드 결제 취소는 외부 PG 별도 처리 (stub)
                saga.transition(PaymentSagaStatus.FAILED)
                paymentSagaRepository.save(saga)
                logger.info("SAGA:COMPENSATION:SKIPPED orderId=${dto.orderId} (no balance deducted, payMethod=${payMethod})")
            }

            throw e
        }
    }
}
