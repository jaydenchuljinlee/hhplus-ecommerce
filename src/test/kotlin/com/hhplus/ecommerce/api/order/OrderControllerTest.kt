package com.hhplus.ecommerce.api.order

import com.hhplus.ecommerce.api.order.controller.OrderController
import com.hhplus.ecommerce.api.order.dto.OrderCreationRequest
import com.hhplus.ecommerce.usercase.order.OrderFacade
import com.hhplus.ecommerce.usercase.order.dto.OrderCreation
import com.hhplus.ecommerce.usercase.order.dto.OrderInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class OrderControllerTest {
    @Mock
    private lateinit var orderFacade: OrderFacade

    private lateinit var orderController: OrderController

    @BeforeEach
    fun beforeEach() {
        orderController = OrderController(orderFacade)
    }

    @DisplayName("success: 주문 API")
    @Test
    fun successOrderPrepared() {
        // Given
        val param = OrderCreation(
            userId = 0L,
            productId = 0L,
            price = 1000,
            quantity = 2
        )

        val result = OrderInfo(
            orderId = 0L,
            userId = 0L,
            productId = 0L,
            quantity = 2,
            price = 1000,
            status = "ORDER_COMPLETED"
        )

        BDDMockito.given(orderFacade.order(param)).willReturn(result)

        val request = OrderCreationRequest(
            userId = 0,
            productId = 0,
            price = 1000,
            quantity = 2
        )

        // When
        val response = orderController.prepareOrder(request).data!!

        // Then
        assertEquals(response.orderId, 0L)
        assertEquals(response.productId, 0L)
        assertEquals(response.price, 1000)
        assertEquals(response.quantity, 2)
        assertEquals(response.status, "ORDER_COMPLETED")
    }
}