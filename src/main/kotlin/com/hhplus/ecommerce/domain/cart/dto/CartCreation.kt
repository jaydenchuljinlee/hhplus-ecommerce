package com.hhplus.ecommerce.domain.cart.dto

import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity

data class CartCreation(
    var userId: Long,
    var productId: Long,
    var quantity: Int
) {
    fun toEntity() = CartEntity(
        userId = userId,
        productId = productId,
        quantity = quantity
    )
}