package com.hhplus.ecommerce.facade

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderDetailResult
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.order.domain.dto.OrderResult
import com.hhplus.ecommerce.payment.common.PayStatus
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.dto.PaymentResult
import com.hhplus.ecommerce.payment.usecase.PaymentFacade
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.BDDMockito.then
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

    private fun makeOrderResult(userId: Long, orderId: Long): OrderResult {
        val detail = OrderDetailResult(id = 2, productId = 2, quantity = 5, price = 100)
        return OrderResult(
            orderId = orderId,
            userId = userId,
            totalPrice = listOf(detail).sumOf { it.quantity * it.price },
            totalQuantity = listOf(detail).sumOf { it.quantity },
            status = OrderStatus.STOCK_CONFIRMED,
            details = listOf(detail)
        )
    }

    @DisplayName("정상 결제 — 잔액 차감 → 결제 생성 → 주문 확정 순서로 처리된다")
    @Test
    fun paymentTest() {
        val userId = 1L
        val orderId = 2L
        val orderResult = makeOrderResult(userId, orderId)

        BDDMockito.given(orderService.getOrder(OrderQuery(orderId, OrderStatus.STOCK_CONFIRMED)))
            .willReturn(orderResult)

        val paymentCommand = CreationPaymentCommand(userId = userId, orderId = orderId, price = orderResult.totalPrice)
        val paymentResult = PaymentResult(
            paymentId = 1, userId = userId, orderId = orderId,
            status = PayStatus.PAID, price = orderResult.totalPrice
        )
        BDDMockito.given(paymentService.pay(paymentCommand)).willReturn(paymentResult)

        val paymentInfo = paymentFacade.pay(PaymentCreation(userId = userId, orderId = orderId))

        assertEquals(paymentInfo.paymentId, paymentResult.paymentId)
        assertEquals(paymentInfo.userId, paymentResult.userId)
        assertEquals(paymentInfo.orderId, paymentResult.orderId)
        assertEquals(paymentInfo.price, paymentResult.price)
        assertEquals(paymentInfo.status, paymentResult.status)
    }

    @DisplayName("결제 생성 실패 — 잔액 차감 후 paymentService 예외 발생 시 잔액이 환불된다")
    @Test
    fun paymentFail_shouldRefundBalance() {
        val userId = 1L
        val orderId = 2L
        val orderResult = makeOrderResult(userId, orderId)

        BDDMockito.given(orderService.getOrder(OrderQuery(orderId, OrderStatus.STOCK_CONFIRMED)))
            .willReturn(orderResult)
        BDDMockito.given(paymentService.pay(
            CreationPaymentCommand(userId = userId, orderId = orderId, price = orderResult.totalPrice)
        )).willThrow(RuntimeException("결제 생성 실패"))

        assertThrows<RuntimeException> {
            paymentFacade.pay(PaymentCreation(userId = userId, orderId = orderId))
        }

        // 잔액 환불(charge) 호출 여부 검증
        then(balanceService).should().charge(
            BalanceTransaction(userId = userId, amount = orderResult.totalPrice, type = BalanceTransaction.TransactionType.CHARGE)
        )
    }

    @DisplayName("주문 확정 실패 — 결제 생성 후 orderComplete 예외 발생 시 잔액이 환불된다")
    @Test
    fun orderCompleteFail_shouldRefundBalance() {
        val userId = 1L
        val orderId = 2L
        val orderResult = makeOrderResult(userId, orderId)

        BDDMockito.given(orderService.getOrder(OrderQuery(orderId, OrderStatus.STOCK_CONFIRMED)))
            .willReturn(orderResult)

        val paymentCommand = CreationPaymentCommand(userId = userId, orderId = orderId, price = orderResult.totalPrice)
        val paymentResult = PaymentResult(
            paymentId = 1, userId = userId, orderId = orderId,
            status = PayStatus.PAID, price = orderResult.totalPrice
        )
        BDDMockito.given(paymentService.pay(paymentCommand)).willReturn(paymentResult)
        BDDMockito.willThrow(RuntimeException("주문 확정 실패"))
            .given(orderService).orderComplete(OrderCompleteCommand(orderId))

        assertThrows<RuntimeException> {
            paymentFacade.pay(PaymentCreation(userId = userId, orderId = orderId))
        }

        // 잔액 환불(charge) 호출 여부 검증
        then(balanceService).should().charge(
            BalanceTransaction(userId = userId, amount = orderResult.totalPrice, type = BalanceTransaction.TransactionType.CHARGE)
        )
    }
}
