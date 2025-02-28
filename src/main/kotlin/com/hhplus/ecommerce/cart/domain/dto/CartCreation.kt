package com.hhplus.ecommerce.cart.domain.dto

import com.hhplus.ecommerce.cart.infrastructure.jpa.entity.CartEntity

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