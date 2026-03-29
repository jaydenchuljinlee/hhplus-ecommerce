package com.hhplus.ecommerce.payment.usecase

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.notification.common.NotificationChannel
import com.hhplus.ecommerce.notification.common.NotificationType
import com.hhplus.ecommerce.notification.domain.INotificationEventPublisher
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.payment.common.PaymentSagaStatus
import com.hhplus.ecommerce.payment.domain.PaymentSagaService
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.usecase.dto.PaymentBreakdown
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import com.hhplus.ecommerce.payment.usecase.dto.PaymentInfo
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.user.domain.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PaymentFacade(
    private val balanceService: BalanceService,
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val stockReservationService: StockReservationService,
    private val paymentSagaService: PaymentSagaService,
    private val notificationEventPublisher: INotificationEventPublisher,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(PaymentFacade::class.java)

    fun pay(dto: PaymentCreation): PaymentInfo {
        val saga = paymentSagaService.start(dto.orderId, dto.userId)

        val order = orderService.getOrder(
            OrderQuery(orderId = dto.orderId, status = OrderStatus.REQUESTED)
        )

        val breakdown = dto.breakdown ?: PaymentBreakdown(balanceAmount = order.totalPrice)

        var balanceDeducted = false
        var pointDeducted = false
        var stockCommitted = false

        try {
            // 1. 잔액 차감
            if (breakdown.balanceAmount > 0) {
                balanceService.use(
                    BalanceTransaction(
                        userId = dto.userId,
                        amount = breakdown.balanceAmount,
                        type = BalanceTransaction.TransactionType.USE
                    )
                )
                balanceDeducted = true
            }

            // 2. 포인트 차감
            if (breakdown.pointAmount > 0) {
                userService.usePoint(dto.userId, breakdown.pointAmount)
                pointDeducted = true
            }

            paymentSagaService.updateStatusByOrderId(dto.orderId, PaymentSagaStatus.BALANCE_DEDUCTED)

            // 3. 결제 생성
            val result = paymentService.pay(
                CreationPaymentCommand(
                    orderId = dto.orderId,
                    userId = dto.userId,
                    price = order.totalPrice
                )
            )
            paymentSagaService.updateStatusByOrderId(
                dto.orderId, PaymentSagaStatus.PAYMENT_CREATED, paymentId = result.paymentId
            )

            // 4. 주문 확정
            orderService.orderComplete(OrderCompleteCommand(dto.orderId))
            paymentSagaService.updateStatusByOrderId(dto.orderId, PaymentSagaStatus.ORDER_CONFIRMED)

            // 5. 재고 확정
            stockReservationService.commit(dto.orderId)
            stockCommitted = true
            paymentSagaService.updateStatusByOrderId(dto.orderId, PaymentSagaStatus.STOCK_COMMITTED)

            paymentSagaService.updateStatusByOrderId(dto.orderId, PaymentSagaStatus.COMPLETED)

            // 구매 금액 누적 및 등급 재산정
            userService.addPurchaseAmount(dto.userId, order.totalPrice)

            notificationEventPublisher.publish(
                NotificationEvent(
                    userId = dto.userId,
                    type = NotificationType.PAYMENT_CONFIRMED,
                    channel = NotificationChannel.PUSH,
                    title = "결제가 완료되었습니다.",
                    body = "주문 번호 ${dto.orderId}번 결제가 정상적으로 완료되었습니다.",
                    orderId = dto.orderId
                )
            )

            return PaymentInfo.from(result)

        } catch (e: Exception) {
            compensate(saga, dto, breakdown, balanceDeducted, pointDeducted, stockCommitted, e)
            throw e
        }
    }

    private fun compensate(
        saga: PaymentSagaEntity,
        dto: PaymentCreation,
        breakdown: PaymentBreakdown,
        balanceDeducted: Boolean,
        pointDeducted: Boolean,
        stockCommitted: Boolean,
        cause: Exception
    ) {
        paymentSagaService.updateStatusByOrderId(
            dto.orderId, PaymentSagaStatus.COMPENSATING, failReason = cause.message
        )
        try {
            if (stockCommitted) {
                stockReservationService.release(dto.orderId)
            }
            if (pointDeducted) {
                userService.chargePoint(dto.userId, breakdown.pointAmount)
            }
            if (balanceDeducted) {
                balanceService.charge(
                    BalanceTransaction(
                        userId = dto.userId,
                        amount = breakdown.balanceAmount,
                        type = BalanceTransaction.TransactionType.CHARGE
                    )
                )
            }
            paymentSagaService.updateStatusByOrderId(dto.orderId, PaymentSagaStatus.FAILED)
        } catch (compensationError: Exception) {
            logger.warn(
                "SAGA 보상 트랜잭션 실패 - sagaId=${saga.id}, orderId=${dto.orderId}, cause=${compensationError.message}"
            )
            paymentSagaService.updateStatusByOrderId(
                dto.orderId, PaymentSagaStatus.COMPENSATION_FAILED, failReason = compensationError.message
            )
        }
    }
}
