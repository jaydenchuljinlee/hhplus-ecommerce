package com.hhplus.ecommerce.api.cart.dto

class CartResponse {
    data class Item(
        var cartId: Long,
        var userId: Long,
        var productId: Long,
    ) {
        companion object {
            fun getInstance() = Item(
                cartId = 0L,
                userId = 0L,
                productId = 0L,
            )
        }
    }
}