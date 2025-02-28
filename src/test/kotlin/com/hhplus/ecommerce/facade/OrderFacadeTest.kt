package com.hhplus.ecommerce.facade

import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.cart.domain.CartService
import com.hhplus.ecommerce.cart.domain.dto.CartResult
import com.hhplus.ecommerce.cart.domain.dto.ProductIdCartQuery
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderCreationCommand
import com.hhplus.ecommerce.order.domain.dto.OrderResult
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.dto.ProductDetailQuery
import com.hhplus.ecommerce.product.domain.dto.ProductDetailResult
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.user.domain.dto.UserQuery
import com.hhplus.ecommerce.user.domain.dto.UserResult
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class OrderFacadeTest {
    @Mock
    private lateinit var userService: UserService
    @Mock
    private lateinit var productService: ProductService
    @Mock
    private lateinit var balanceService: BalanceService
    @Mock
    private lateinit var orderService: OrderService
    @Mock
    private lateinit var cartService: CartService

    private lateinit var orderFacade: OrderFacade

    @BeforeEach
    fun before() {
        orderFacade = OrderFacade(userService, productService, balanceService, orderService, cartService)
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

        val productQuery = ProductDetailQuery(2)
        val productResult = ProductDetailResult(
            productId = 2,
            productDetailId = 3,
            quantity = 10,
        )
        BDDMockito.given(productService.getProductDetail(productQuery)).willReturn(productResult)


        val orderCommand = OrderCreationCommand(
            userId = userQuery.userId,
            productId = productResult.productId,
            quantity = 5,
            price = 200
        )

        val orderResult = OrderResult(
            orderId = 4,
            userId = userResult.userId,
            productId = productResult.productId,
            quantity = productResult.quantity - orderCommand.quantity,
            price = orderCommand.price,
            totalPrice = orderCommand.price * orderCommand.quantity,
            status =  "ORDER_REQUEST"
        )

        BDDMockito.given(orderService.order(orderCommand)).willReturn(orderResult)

        val cartQuery = ProductIdCartQuery(
            productId = productResult.productId
        )

        val cartResult = CartResult(
            cartId = 5,
            userId = userResult.userId,
            productId = productResult.productId,
            quantity = 1
        )

        BDDMockito.given(cartService.getCartByProduct(cartQuery)).willReturn(cartResult)

        val orderCreation = OrderCreation(
            userId = userQuery.userId,
            productId = productResult.productId,
            price = orderCommand.price,
            quantity = orderCommand.quantity
        )

        val orderInfo = orderFacade.order(orderCreation)

        assertEquals(orderInfo.orderId, orderResult.orderId)
        assertEquals(orderInfo.userId, orderResult.userId)
        assertEquals(orderInfo.productId, orderResult.productId)
        assertEquals(orderInfo.price, orderResult.price)
        assertEquals(orderInfo.quantity, orderResult.quantity)
        assertEquals(orderInfo.status, orderResult.status)
    }
}