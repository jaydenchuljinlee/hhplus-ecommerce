package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

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
            totalPrice = quantity * price,
            status = OrderStatus.REQUESTED
        )
    }
}