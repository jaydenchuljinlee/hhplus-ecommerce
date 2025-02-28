package com.hhplus.ecommerce.cart.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.cart.api.dto.CartCreationRequest
import com.hhplus.ecommerce.cart.api.dto.CartDeletionRequest
import com.hhplus.ecommerce.cart.api.dto.CartListQueryRequest
import com.hhplus.ecommerce.cart.api.dto.CartResponse
import com.hhplus.ecommerce.cart.domain.CartService
import org.springframework.web.bind.annotation.*


@RestController
class CartController(
    private val cartService: CartService
): ICartController {

    override fun addCart(request: CartCreationRequest): CustomApiResponse<CartResponse> {
        val result = cartService.add(request.toCartCreation())

        return CustomApiResponse.success(CartResponse.toCartResponse(result))
    }

    override fun deleteCart(request: CartDeletionRequest): CustomApiResponse<Long> {
        val result = cartService.delete(request.toCartDeletion())

        return CustomApiResponse.success(result)
    }

    override fun getCartList(request: CartListQueryRequest): CustomApiResponse<List<CartResponse>> {
        val results = cartService.getCartList(request.toCartListQuery())
        val response = results.map { CartResponse.toCartResponse(it) }
        return CustomApiResponse.success(response)
    }
}