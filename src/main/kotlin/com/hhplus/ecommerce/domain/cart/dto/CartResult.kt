package com.hhplus.ecommerce.domain.cart.dto

import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity

data class CartResult(
    var cartId: Long,
    var userId: Long,
    var productId: Long,
    var quantity: Int,
) {
    companion object {
        fun from(cartEntity: CartEntity): CartResult {
            return CartResult(
                cartId = cartEntity.id,
                userId = cartEntity.userId,
                productId = cartEntity.productId,
                quantity = cartEntity.quantity,
            )
        }
    }


}