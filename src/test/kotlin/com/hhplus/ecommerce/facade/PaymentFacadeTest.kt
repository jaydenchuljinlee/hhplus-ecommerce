package com.hhplus.ecommerce.facade

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderDetailResult
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.order.domain.dto.OrderResult
import com.hhplus.ecommerce.payment.common.PayStatus
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.dto.PaymentResult
import com.hhplus.ecommerce.notification.domain.INotificationEventPublisher
import com.hhplus.ecommerce.payment.common.PaymentSagaStatus
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.payment.domain.PaymentSagaService
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.usecase.PaymentFacade
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import com.hhplus.ecommerce.product.domain.StockReservationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
    @Mock
    private lateinit var stockReservationService: StockReservationService
    @Mock
    private lateinit var paymentSagaService: PaymentSagaService
    @Mock
    private lateinit var notificationEventPublisher: INotificationEventPublisher
    @Mock
    private lateinit var userService: UserService

    private lateinit var paymentFacade: PaymentFacade

    @BeforeEach
    fun before() {
        paymentFacade = PaymentFacade(balanceService, paymentService, orderService, stockReservationService, paymentSagaService, notificationEventPublisher, userService)

        val mockSaga = PaymentSagaEntity(id = 1L, orderId = 0L, userId = 0L, sagaStatus = PaymentSagaStatus.STARTED)
        BDDMockito.given(paymentSagaService.start(BDDMockito.anyLong(), BDDMockito.anyLong())).willReturn(mockSaga)
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
            status = OrderStatus.REQUESTED
        )

        val orderDetailResult = OrderDetailResult(
            id = 2,
            productId = 2,
            quantity = 5,
            price = 100
        )

        val orderResult = OrderResult(
            orderId = 2,
            userId = balanceResult.userId,
            totalPrice = listOf(orderDetailResult).sumOf { it.price * it.quantity },
            totalQuantity = listOf(orderDetailResult).sumOf { it.quantity },
            status =  OrderStatus.REQUESTED,
            details = listOf(orderDetailResult)
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
            status = PayStatus.PAID,
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

    @DisplayName("결제 완료 시 stockReservationService.commit(orderId)가 호출된다.")
    @Test
    fun payInvokesStockReservationCommitWithOrderId() {
        // Given
        val orderId = 2L
        val userId = 1L

        val orderQuery = OrderQuery(
            orderId = orderId,
            status = OrderStatus.REQUESTED
        )

        val orderDetailResult = OrderDetailResult(
            id = 2,
            productId = 2,
            quantity = 5,
            price = 100
        )

        val orderResult = OrderResult(
            orderId = orderId,
            userId = userId,
            totalPrice = listOf(orderDetailResult).sumOf { it.price * it.quantity },
            totalQuantity = listOf(orderDetailResult).sumOf { it.quantity },
            status = OrderStatus.REQUESTED,
            details = listOf(orderDetailResult)
        )

        BDDMockito.given(orderService.getOrder(orderQuery)).willReturn(orderResult)

        val paymentCommand = CreationPaymentCommand(
            userId = userId,
            orderId = orderId,
            price = orderResult.totalPrice
        )

        val paymentResult = PaymentResult(
            paymentId = 1,
            userId = userId,
            orderId = orderId,
            status = PayStatus.PAID,
            price = orderResult.totalPrice,
        )

        BDDMockito.given(paymentService.pay(paymentCommand)).willReturn(paymentResult)

        // When
        paymentFacade.pay(PaymentCreation(userId = userId, orderId = orderId))

        // Then
        then(stockReservationService).should().commit(orderId)
    }
}
