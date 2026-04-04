package com.hhplus.ecommerce.facade

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCreationCommand
import com.hhplus.ecommerce.order.domain.dto.OrderDetailCreationCommand
import com.hhplus.ecommerce.order.domain.dto.OrderDetailResult
import com.hhplus.ecommerce.order.domain.dto.OrderResult
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.product.domain.dto.ProductDetailResult
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.user.domain.dto.UserQuery
import com.hhplus.ecommerce.user.domain.dto.UserResult
import com.hhplus.ecommerce.notification.domain.INotificationEventPublisher
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class OrderFacadeTest {
    @Mock
    private lateinit var userService: UserService
    @Mock
    private lateinit var balanceService: BalanceService
    @Mock
    private lateinit var orderService: OrderService
    @Mock
    private lateinit var stockReservationService: StockReservationService
    @Mock
    private lateinit var productService: ProductService
    @Mock
    private lateinit var notificationEventPublisher: INotificationEventPublisher

    private lateinit var orderFacade: OrderFacade

    @BeforeEach
    fun before() {
        orderFacade = OrderFacade(userService, balanceService, orderService, stockReservationService, productService, notificationEventPublisher)
    }

    @DisplayName("주문 정합성 테스트")
    @Test
    fun orderTest() {
        val userQuery = UserQuery(1)
        val userResult = UserResult(
            userId = 1,
            userName = "이철진",
            phone = "01012345678"
        )
        BDDMockito.given(userService.getUserById(userQuery)).willReturn(userResult)

        val productResult = ProductDetailResult(
            productId = 2,
            productDetailId = 3,
            quantity = 10,
        )

        val orderDetailCommand = OrderDetailCreationCommand(
            productId = productResult.productId,
            quantity = 5,
            price = 200
        )

        val orderCommand = OrderCreationCommand(
            userId = userQuery.userId,
            details = listOf(orderDetailCommand)
        )

        val orderDetailResult = OrderDetailResult(
            id = 0,
            productId = productResult.productId,
            quantity = orderDetailCommand.quantity,
            price = orderDetailCommand.price,
        )

        val orderResult = OrderResult(
            orderId = 4,
            userId = userResult.userId,
            details = listOf(orderDetailResult),
            totalPrice = listOf(orderDetailResult).map { it.price }.sum(),
            totalQuantity = listOf(orderDetailResult).map { it.quantity }.sum(),
            status =  OrderStatus.REQUESTED
        )

        BDDMockito.given(orderService.order(orderCommand)).willReturn(orderResult)

        val orderDetailCreation = OrderDetailCreation(
            productId = productResult.productId,
            price = orderDetailCommand.price,
            quantity = orderDetailCommand.quantity
        )

        val orderCreation = OrderCreation(
            userId = userQuery.userId,
            details = listOf(orderDetailCreation)
        )

        val orderInfo = orderFacade.order(orderCreation)

        assertEquals(orderInfo.orderId, orderResult.orderId)
        assertEquals(orderInfo.userId, orderResult.userId)
        assertEquals(orderInfo.status, orderResult.status)
        orderInfo.details.forEach { info ->
            assertEquals(info.productId, orderDetailResult.productId)
            assertEquals(info.price, orderDetailResult.price)
            assertEquals(info.quantity, orderDetailResult.quantity)
        }

        verify(stockReservationService).reserve(orderResult.orderId, productResult.productId, orderDetailCommand.quantity)
    }
}
