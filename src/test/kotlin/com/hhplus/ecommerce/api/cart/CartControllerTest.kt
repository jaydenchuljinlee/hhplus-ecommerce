package com.hhplus.ecommerce.api.cart

import com.hhplus.ecommerce.api.cart.controller.CartController
import com.hhplus.ecommerce.api.cart.dto.CartRequest
import com.hhplus.ecommerce.common.exception.cart.CartNotFoundException
import com.hhplus.ecommerce.common.exception.cart.DuplicatedProductException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CartControllerTest {
    private lateinit var cartController: CartController

    @BeforeEach
    fun before() {
        cartController = CartController()
    }

    @DisplayName("success: 장바구니 추가 API")
    @Test
    fun successAddCartTest() {
        // Given
        val request = CartRequest.Add(0L, 0L)

        // When
        val result = cartController.addCart(request)

        // Then

    }

    @DisplayName("success: 장바구니 삭제 API")
    @Test
    fun successDeleteCartTest() {
        // Given
        val request = CartRequest.Delete(0L)

        // When & Then
        cartController.deleteCart(request)
    }

    @DisplayName("success: 장바구니 조회 API")
    fun successCartListTest() {
        // Given & When & Then
        cartController.getCartList()
    }

    @DisplayName("중복 된 장바구니가 존재하면, DuplicatedProductException이 발생한다.")
    @Test
    fun duplicatedProductExceptionTest() {
        // Given
        val request = CartRequest.Add(0L, 1L)

        // When
        val exception = assertThrows<DuplicatedProductException> {
            cartController.addCart(request)
        }

        // Then
        assertEquals(exception.message, "같은 상품이 장바구니에 존재합니다.")
    }

    @DisplayName("삭제 API에서 장바구니 정보가 존재하지 않으면, CartNotFoundException이 발생한다.")
    @Test
    fun cartNotFoundExceptionTest() {
        // Given
        val request = CartRequest.Delete(1L)

        // When
        val exception = assertThrows<CartNotFoundException> {
            cartController.deleteCart(request)
        }

        // Then
        assertEquals(exception.message, "존재하지 않는 장바구니입니다.")
    }

}