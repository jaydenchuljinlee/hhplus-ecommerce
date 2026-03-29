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

    @DisplayName("reserve 호출 시 availableQuantity 기준으로 검증 후 reservedQuantity가 증가한다.")
    @Test
    fun reserveIncreasesReservedQuantityByAmount() {
        // Given
        val productDetailEntity = ProductDetailEntity(id = 1L, productId = 1L, productOptionId = 1L, quantity = 10)

        // When
        productDetailEntity.reserve(3)

        // Then
        assertEquals(3, productDetailEntity.reservedQuantity)
        assertEquals(7, productDetailEntity.availableQuantity)
    }

    @DisplayName("availableQuantity보다 많은 수량을 reserve하면 OutOfStockException이 발생한다.")
    @Test
    fun reserveThrowsOutOfStockExceptionWhenAmountExceedsAvailableQuantity() {
        // Given
        val productDetailEntity = ProductDetailEntity(id = 1L, productId = 1L, productOptionId = 1L, quantity = 5)
        productDetailEntity.reserve(3)

        // When & Then
        assertThrows<OutOfStockException> {
            productDetailEntity.reserve(3)
        }
    }

    @DisplayName("commit 호출 시 reservedQuantity와 quantity가 함께 감소한다.")
    @Test
    fun commitDecreasesReservedQuantityAndQuantityTogether() {
        // Given
        val productDetailEntity = ProductDetailEntity(id = 1L, productId = 1L, productOptionId = 1L, quantity = 10)
        productDetailEntity.reserve(4)

        // When
        productDetailEntity.commit(4)

        // Then
        assertEquals(0, productDetailEntity.reservedQuantity)
        assertEquals(6, productDetailEntity.quantity)
    }

    @DisplayName("reservedQuantity보다 많은 수량을 commit하면 IllegalArgumentException이 발생한다.")
    @Test
    fun commitThrowsIllegalArgumentExceptionWhenAmountExceedsReservedQuantity() {
        // Given
        val productDetailEntity = ProductDetailEntity(id = 1L, productId = 1L, productOptionId = 1L, quantity = 10)
        productDetailEntity.reserve(2)

        // When & Then
        assertThrows<IllegalArgumentException> {
            productDetailEntity.commit(5)
        }
    }

    @DisplayName("release 호출 시 reservedQuantity만 감소하고 quantity는 복원된다.")
    @Test
    fun releaseDecreasesOnlyReservedQuantityAndRestoresAvailableQuantity() {
        // Given
        val productDetailEntity = ProductDetailEntity(id = 1L, productId = 1L, productOptionId = 1L, quantity = 10)
        productDetailEntity.reserve(4)

        // When
        productDetailEntity.release(4)

        // Then
        assertEquals(0, productDetailEntity.reservedQuantity)
        assertEquals(10, productDetailEntity.quantity)
        assertEquals(10, productDetailEntity.availableQuantity)
    }

    @DisplayName("reservedQuantity보다 많은 수량을 release하면 IllegalArgumentException이 발생한다.")
    @Test
    fun releaseThrowsIllegalArgumentExceptionWhenAmountExceedsReservedQuantity() {
        // Given
        val productDetailEntity = ProductDetailEntity(id = 1L, productId = 1L, productOptionId = 1L, quantity = 10)
        productDetailEntity.reserve(2)

        // When & Then
        assertThrows<IllegalArgumentException> {
            productDetailEntity.release(5)
        }
    }
}
