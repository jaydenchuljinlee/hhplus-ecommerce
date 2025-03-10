package com.hhplus.ecommerce.facade

import com.hhplus.ecommerce.domain.balance.BalanceService
import com.hhplus.ecommerce.domain.balance.dto.BalanceResult
import com.hhplus.ecommerce.domain.order.OrderService
import com.hhplus.ecommerce.domain.order.dto.OrderQuery
import com.hhplus.ecommerce.domain.order.dto.OrderResult
import com.hhplus.ecommerce.domain.payment.PaymentService
import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.domain.payment.dto.PaymentResult
import com.hhplus.ecommerce.usercase.payment.PaymentFacade
import com.hhplus.ecommerce.usercase.payment.dto.PaymentCreation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class PaymentFacadeTest {
    @Mock
    private lateinit var balanceService: BalanceService
    @Mock
    private lateinit var paymentService: PaymentService
    @Mock
    private lateinit var orderService: OrderService

    private lateinit var paymentFacade: PaymentFacade

    @BeforeEach
    fun before() {
        paymentFacade = PaymentFacade(balanceService, paymentService, orderService)
    }

    @DisplayName("결제 정합성 테스트")
    @Test
    fun paymentTest() {
        val balanceResult = BalanceResult(
            userId = 1,
            balance = 10_000
        )

        val orderQuery = OrderQuery(
            orderId = 2,
            status = "ORDER_REQUEST"
        )

        val orderResult = OrderResult(
            orderId = 2,
            userId = balanceResult.userId,
            productId = 2,
            quantity = 5,
            price = 100,
            totalPrice = 500,
            status =  "ORDER_REQUEST"
        )

        BDDMockito.given(orderService.getOrder(orderQuery)).willReturn(orderResult)

        val paymentCommand = CreationPaymentCommand(
            userId = balanceResult.userId,
            orderId = orderResult.orderId,
            price = orderResult.totalPrice
        )

        val paymentResult = PaymentResult(
            paymentId = 1,
            userId = balanceResult.userId,
            orderId = orderResult.orderId,
            status = "PAYMENT_COMPLETED",
            price = orderResult.totalPrice,
        )

        BDDMockito.given(paymentService.pay(paymentCommand)).willReturn(paymentResult)

        val paymentCreation = PaymentCreation(
            userId = balanceResult.userId,
            orderId = orderResult.orderId,
        )

        val paymentInfo = paymentFacade.pay(paymentCreation)

        assertEquals(paymentInfo.paymentId, paymentResult.paymentId)
        assertEquals(paymentInfo.userId, paymentResult.userId)
        assertEquals(paymentInfo.orderId, paymentResult.orderId)
        assertEquals(paymentInfo.price, paymentResult.price)
        assertEquals(paymentInfo.status, paymentResult.status)

    }
}