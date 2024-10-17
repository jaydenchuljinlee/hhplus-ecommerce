package com.hhplus.ecommerce.domain.order.dto

import com.hhplus.ecommerce.infrastructure.order.jpa.entity.OrderEntity

data class OrderResult(
    var orderId: Long,
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    var price: Long,
    var totalPrice: Long,
    var status: String,
) {
    companion object {
        fun from(entity: OrderEntity) = OrderResult(
            orderId = entity.id,
            userId = entity.userId,
            productId = entity.productId,
            quantity = entity.quantity,
            price = entity.price,
            totalPrice = entity.totalPrice,
            status = entity.status
        )
    }
}