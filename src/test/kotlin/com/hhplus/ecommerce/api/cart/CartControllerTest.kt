package com.hhplus.ecommerce.api.cart

import com.hhplus.ecommerce.cart.api.CartController
import com.hhplus.ecommerce.cart.api.dto.CartCreationRequest
import com.hhplus.ecommerce.cart.api.dto.CartDeletionRequest
import com.hhplus.ecommerce.cart.api.dto.CartListQueryRequest
import com.hhplus.ecommerce.cart.domain.CartService
import com.hhplus.ecommerce.cart.domain.dto.CartCreation
import com.hhplus.ecommerce.cart.domain.dto.CartDeletion
import com.hhplus.ecommerce.cart.domain.dto.CartListQuery
import com.hhplus.ecommerce.cart.domain.dto.CartResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CartControllerTest {
    @Mock
    private lateinit var cartService: CartService

    private lateinit var cartController: CartController

    @BeforeEach
    fun before() {
        cartController = CartController(cartService)
    }

    @DisplayName("success: 장바구니 추가 API")
    @Test
    fun successAddCartTest() {
        // Given
        val param = CartCreation(
            0L, 0L,  100
        )

        val result = CartResult(
            cartId = 0L,
            userId = 0L,
            productId = 0L,
            quantity = 100
        )

        BDDMockito.given(cartService.add(param)).willReturn(result)

        val request = CartCreationRequest(0L, 0L, 100)

        // When
        val response = cartController.addCart(request).data!!

        // Then
        assertEquals(response.cartId, 0L)
        assertEquals(response.productId, 0L)
        assertEquals(response.quantity, 100)

    }

    @DisplayName("success: 장바구니 삭제 API")
    @Test
    fun successDeleteCartTest() {
        // Given
        val param = CartDeletion(0L)

        BDDMockito.given(cartService.delete(param)).willReturn(0)

        val request = CartDeletionRequest(0L)

        // When
        val result = cartController.deleteCart(request).data!!

        // Then
        assertEquals(result, 0L)
    }

    @DisplayName("success: 장바구니 조회 API")
    @Test
    fun successCartListTest() {
        // Given & When & Then
        val param = CartListQuery(0L)

        val result = CartResult(
            cartId = 0L,
            userId = 0L,
            productId = 0L,
            quantity = 100
        )

        BDDMockito.given(cartService.getCartList(param)).willReturn(listOf(result))

        val request = CartListQueryRequest(0L)

        val results = cartController.getCartList(request).data!!

        assertEquals(results.size, 1)
        assertEquals(results[0].cartId, 0L)
        assertEquals(results[0].userId, 0L)
        assertEquals(results[0].productId, 0L)
        assertEquals(results[0].quantity, 100)
    }
}