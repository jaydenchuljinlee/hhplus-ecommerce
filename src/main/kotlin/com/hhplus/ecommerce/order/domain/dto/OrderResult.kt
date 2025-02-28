package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

data class OrderResult(
    var orderId: Long,
    var userId: Long,
    var productId: Long,
    // var quantity: Int,
    // var price: Long,
    var totalPrice: Long,
    var status: OrderStatus,
) {
    companion object {
        fun from(entity: OrderEntity) = OrderResult(
            orderId = entity.id,
            userId = entity.userId,
            productId = entity.productId,
            // quantity = entity.quantity,
            // price = entity.price,
            totalPrice = entity.totalPrice,
            status = entity.status
        )
    }
}