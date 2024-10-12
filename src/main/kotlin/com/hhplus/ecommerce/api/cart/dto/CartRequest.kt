package com.hhplus.ecommerce.api.cart.dto

class CartRequest {
    data class Add(
        var userId: Long,
        var productId: Long,
    )

    data class Delete(
        var cartId: Long,
    )
}