package com.hhplus.ecommerce.api.cart.dto

class CartResponse(
    var cartId: Long,
    var userId: Long,
    var productId: Long,
) {
    companion object {
        fun getInstance() = CartResponse(
            cartId = 0L,
            userId = 0L,
            productId = 0L,
        )
    }
}