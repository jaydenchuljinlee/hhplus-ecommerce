package com.hhplus.ecommerce.api.cart.controller

import com.hhplus.ecommerce.api.cart.dto.CartRequest
import com.hhplus.ecommerce.api.cart.dto.CartResponse
import com.hhplus.ecommerce.common.exception.cart.CartNotFoundException
import com.hhplus.ecommerce.common.exception.cart.DuplicatedProductException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("cart")
@RestController
class CartController {

    @PostMapping()
    fun addCart(request: CartRequest.Add): CartResponse.Item {
        // 중복 체크
        if (request.productId == 1L) throw DuplicatedProductException()

        return CartResponse.Item.getInstance()
    }

    @DeleteMapping("{productId}")
    fun deleteCart(request: CartRequest.Delete) {
        // 존재 여부 체크
        if (request.cartId == 1L) throw CartNotFoundException()
    }

    @GetMapping("list")
    fun getCartList(): List<CartResponse.Item> {
        return listOf(CartResponse.Item.getInstance())
    }
}