package com.hhplus.ecommerce.domain.cart

import com.hhplus.ecommerce.domain.cart.dto.CartResult
import com.hhplus.ecommerce.domain.cart.dto.ProductIdCartQuery
import com.hhplus.ecommerce.infrastructure.cart.ICartRepository
import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class CartServiceTest {
    @Mock
    private lateinit var cartRepository: ICartRepository

    private lateinit var cartService: CartService

    @BeforeEach
    fun before() {
        cartService = CartService(cartRepository)
    }

    @DisplayName("장바구나가 존재하지 않으면, NULL을 반환한다.")
    @Test
    fun getCartByProductNullTest() {
        // Given
        val productId = 1L

        // Optional.empty()를 반환하도록 수정
        BDDMockito.given(cartRepository.findByProductId(productId)).willReturn(null)

        // When
        val query = ProductIdCartQuery(productId = productId)
        val result = cartService.getCartByProduct(query)

        // Then
        assertEquals(result, null)
    }

    @DisplayName("장바구나가 존재하면, 값을 반환한다.")
    @Test
    fun getCartByProductResultTest() {
        // Given
        val productId = 1L
        val cartEntity = CartEntity(
            id = 1L,
            userId = 1L,
            productId = productId,
            quantity = 10
        )

        // Optional.empty()를 반환하도록 수정
        BDDMockito.given(cartRepository.findByProductId(productId)).willReturn(cartEntity)

        // When
        val query = ProductIdCartQuery(productId = productId)
        val result = cartService.getCartByProduct(query)

        // Then
        assertEquals(result != null, true)
    }
}