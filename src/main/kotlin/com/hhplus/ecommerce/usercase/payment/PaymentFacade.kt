package com.hhplus.ecommerce.usercase.payment

import com.hhplus.ecommerce.domain.balance.BalanceService
import com.hhplus.ecommerce.domain.balance.dto.BalanceQuery
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import com.hhplus.ecommerce.domain.cart.CartService
import com.hhplus.ecommerce.domain.order.OrderService
import com.hhplus.ecommerce.domain.order.dto.OrderQuery
import com.hhplus.ecommerce.domain.payment.PaymentService
import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.usercase.payment.dto.PaymentCreation
import com.hhplus.ecommerce.usercase.payment.dto.PaymentInfo
import org.hibernate.query.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val balanceService: BalanceService,
    private val paymentService: PaymentService,
    private val orderService: OrderService)
{

    @Transactional
    fun pay(dto: PaymentCreation): PaymentInfo {
        val balanceQuery = BalanceQuery(dto.userId)
        val balance = balanceService.getBalanceWithLock(balanceQuery)

        val orderQuery = OrderQuery(
            orderId = dto.orderId,
            status = "ORDER_REQUEST"
        )
        val order = orderService.getOrder(orderQuery)

        val balanceToUseCommand = BalanceTransaction(
            userId = balance.userId,
            amount = order.quantity * order.price,
            type = BalanceTransaction.TransactionType.USE
        )

        balanceService.use(balanceToUseCommand)

        val paymentCreation = CreationPaymentCommand(
            orderId = dto.orderId,
            userId = dto.userId,
            price = order.quantity * order.price,
        )

        val result = paymentService.pay(paymentCreation)

        return PaymentInfo.from(result)
    }
}