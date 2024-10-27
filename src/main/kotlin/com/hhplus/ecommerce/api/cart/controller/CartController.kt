package com.hhplus.ecommerce.api.cart.controller

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.api.cart.dto.CartCreationRequest
import com.hhplus.ecommerce.api.cart.dto.CartDeletionRequest
import com.hhplus.ecommerce.api.cart.dto.CartListQueryRequest
import com.hhplus.ecommerce.api.cart.dto.CartResponse
import com.hhplus.ecommerce.domain.cart.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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