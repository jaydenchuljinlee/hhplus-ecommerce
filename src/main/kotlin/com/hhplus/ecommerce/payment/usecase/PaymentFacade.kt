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
import org.springframework.stereotype.Component

@Component
class PaymentFacade(
    private val balanceService: BalanceService,
    private val paymentService: PaymentService,
    private val orderService: OrderService
) {

    fun pay(dto: PaymentCreation): PaymentInfo {
        // 재고 확보가 완료된 주문만 결제 가능 (STOCK_CONFIRMED)
        // REQUESTED 상태에서의 결제 시도는 OrderNotFoundException으로 거부됨
        val orderQuery = OrderQuery(
            orderId = dto.orderId,
            status = OrderStatus.STOCK_CONFIRMED
        )
        val order = orderService.getOrder(orderQuery)

        val balanceToUseCommand = BalanceTransaction(
            userId = dto.userId,
            amount = order.totalPrice,  // totalPrice = sum(수량 × 단가), 총수량을 다시 곱하지 않음
            type = BalanceTransaction.TransactionType.USE
        )

        balanceService.use(balanceToUseCommand)

        val paymentCreation = CreationPaymentCommand(
            orderId = dto.orderId,
            userId = dto.userId,
            price = order.totalPrice,  // totalPrice = sum(수량 × 단가), 총수량을 다시 곱하지 않음
        )

        val result = paymentService.pay(paymentCreation)

        val orderCompleteCommand = OrderCompleteCommand(dto.orderId)

        orderService.orderComplete(orderCompleteCommand)

        return PaymentInfo.from(result)
    }
}