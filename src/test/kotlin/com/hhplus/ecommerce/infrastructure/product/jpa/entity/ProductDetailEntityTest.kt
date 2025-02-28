package com.hhplus.ecommerce.infrastructure.product.jpa.entity

import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ProductDetailEntityTest {
    @DisplayName("상품 재고 감소 시에 재고가 0보다 작으면 OutOfStockException을 발생한다.")
    @Test
    fun decreaseQuantityToZero() {
        // Given
        val productDetailEntity = ProductDetailEntity(1L, 1L, 1L, 1)

        // When
        val exception = assertThrows<OutOfStockException> {
            productDetailEntity.decrease(2)
        }

        // Then
        assertEquals(exception.message, "상품 재고가 부족합니다.")
    }
}