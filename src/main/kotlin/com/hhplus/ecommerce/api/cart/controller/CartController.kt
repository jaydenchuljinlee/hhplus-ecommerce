package com.hhplus.ecommerce.api.cart.controller

import com.hhplus.ecommerce.api.ApiResponse
import com.hhplus.ecommerce.api.cart.dto.CartRequest
import com.hhplus.ecommerce.api.cart.dto.CartResponse
import com.hhplus.ecommerce.common.exception.cart.CartNotFoundException
import com.hhplus.ecommerce.common.exception.cart.DuplicatedProductException
import org.springframework.web.bind.annotation.*

@RequestMapping("cart")
@RestController
class CartController {

    @PostMapping()
    fun addCart(@RequestBody request: CartRequest.Add): ApiResponse<CartResponse.Item> {
        // 중복 체크
        if (request.productId == 1L) throw DuplicatedProductException()

        return ApiResponse.success(CartResponse.Item.getInstance())
    }

    @DeleteMapping()
    fun deleteCart(@RequestBody request: CartRequest.Delete): ApiResponse<Long> {
        // 존재 여부 체크
        if (request.cartId == 1L) throw CartNotFoundException()

        return ApiResponse.success(0L)
    }

    @GetMapping("list")
    fun getCartList(): ApiResponse<List<CartResponse.Item>> {
        val results = listOf(CartResponse.Item.getInstance())
        return ApiResponse.success(results)
    }
}