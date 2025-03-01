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
)
{

    fun pay(dto: PaymentCreation): PaymentInfo {
        val orderQuery = OrderQuery(
            orderId = dto.orderId,
            status = OrderStatus.REQUESTED
        )
        val order = orderService.getOrder(orderQuery)

        val balanceToUseCommand = BalanceTransaction(
            userId = dto.userId,
            amount = order.totalQuantity * order.totalPrice,
            type = BalanceTransaction.TransactionType.USE
        )

        balanceService.use(balanceToUseCommand)

        val paymentCreation = CreationPaymentCommand(
            orderId = dto.orderId,
            userId = dto.userId,
            price = order.totalQuantity * order.totalPrice,
        )

        val result = paymentService.pay(paymentCreation)

        val orderCompleteCommand = OrderCompleteCommand(dto.orderId)

        orderService.orderComplete(orderCompleteCommand)

        return PaymentInfo.from(result)
    }
}