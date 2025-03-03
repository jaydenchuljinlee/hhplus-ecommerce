package com.hhplus.ecommerce.api.order

import com.hhplus.ecommerce.order.api.OrderController
import com.hhplus.ecommerce.order.api.dto.OrderCreationRequest
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.dto.OrderDetailResult
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
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
        val detailParam = OrderDetailCreation(
            productId = 0L,
            price = 1000,
            quantity = 2
        )

        val param = OrderCreation(
            userId = 0L,
            details = listOf(detailParam)
        )

        val detailResult = OrderInfo.DetailInfo(
            productId = 0L,
            quantity = 2,
            price = 1000
        )

        val result = OrderInfo(
            orderId = 0L,
            userId = 0L,
            status = OrderStatus.REQUESTED,
            details = listOf(detailResult)
        )

        BDDMockito.given(orderFacade.order(param)).willReturn(result)

        val detailRequest = OrderCreationRequest.DetailCreationRequest(
            productId = 0,
            price = 1000,
            quantity = 2
        )

        val request = OrderCreationRequest(
            userId = 0,
            details = listOf(detailRequest)
        )

        // When
        val response = orderController.prepareOrder(request).data!!

        // Then
        assertEquals(response.orderId, 0L)
        assertEquals(response.status, OrderStatus.REQUESTED)
        response.details.forEach { res ->
            assertEquals(res.productId, detailRequest.productId)
            assertEquals(res.price, detailRequest.price)
            assertEquals(res.quantity, detailRequest.quantity)
        }
    }
}