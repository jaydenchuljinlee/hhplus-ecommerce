package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderDetailEntity
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

data class OrderDetailCreationCommand(
    var productId: Long,
    var quantity: Int,
    var price: Long
) {

    fun toEntity(order: OrderEntity): OrderDetailEntity {
        return OrderDetailEntity(
            productId = productId,
            quantity = quantity,
            price = price,
            order = order
        )
    }
}