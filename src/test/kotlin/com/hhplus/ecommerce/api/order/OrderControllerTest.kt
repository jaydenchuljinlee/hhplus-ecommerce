package com.hhplus.ecommerce.api.order

import com.hhplus.ecommerce.api.order.controller.OrderController
import com.hhplus.ecommerce.api.order.dto.OrderRequest
import com.hhplus.ecommerce.common.exception.product.OutOfStockException
import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderControllerTest {
    private lateinit var orderController: OrderController

    @BeforeEach
    fun beforeEach() {
        orderController = OrderController()
    }

    @DisplayName("success: 주문 API")
    @Test
    fun successOrderPrepared() {
        // Given
        val request = OrderRequest.Preparation(0L, 0L, 1, 1000)

        // When
        val response = orderController.prepareOrder(request).data!!

        // Then
        assertEquals(response.orderId, 0L)
        assertEquals(response.productId, 0L)
        assertEquals(response.price, 1000)
        assertEquals(response.quantity, 2)
        assertEquals(response.status, "ORDER_COMPLETED")
    }

    @DisplayName("상품 정보가 없으면, ProductNotFoundException이 발생한다")
    @Test
    fun testProductNotFoundException() {
        // Given
        val request = OrderRequest.Preparation(0, 1, 1, 1000)

        // When
        val exception = assertThrows<ProductNotFoundException> {
            orderController.prepareOrder(request)
        }

        // Then
        assertEquals(exception.message, "상품 정보가 존재하지 않습니다.")
    }

    @DisplayName("상품 재고가 없으면, OutOfStockException이 발생한다")
    @Test
    fun testOutOfStockException() {
        // Given
        val request = OrderRequest.Preparation(0, 2, 1, 1000)

        // When
        val exception = assertThrows<OutOfStockException> {
            orderController.prepareOrder(request)
        }

        // Then
        assertEquals(exception.message, "상품 재고가 부족합니다.")
    }
}