package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderDetailEntity

data class OrderDetailResult(
    var id: Long,
    var productId: Long,
    var quantity: Int,
    var price: Long
) {
    companion object {
        fun from(entity: OrderDetailEntity) = OrderDetailResult(
            id = entity.id,
            productId = entity.productId,
            quantity = entity.quantity,
            price = entity.price
        )
    }
}