package com.hhplus.ecommerce.domain.order.dto

import com.hhplus.ecommerce.infrastructure.order.jpa.entity.OrderEntity

data class OrderCreationCommand(
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    var price: Long,
) {
    fun toEntity(): OrderEntity {
        return OrderEntity(
            userId = userId,
            productId = productId,
            quantity = quantity,
            price = price,
            totalPrice = quantity * price,
            status = "ORDER_REQUEST"
        )
    }
}