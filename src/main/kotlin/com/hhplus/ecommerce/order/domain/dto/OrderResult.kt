package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

data class OrderResult(
    var orderId: Long,
    var userId: Long,
    var totalQuantity: Int,
    var totalPrice: Long,
    var status: OrderStatus,
    var details: List<OrderDetailResult> = emptyList()
) {
    companion object {
        fun from(entity: OrderEntity) = OrderResult(
            orderId = entity.id,
            userId = entity.userId,
            totalQuantity = entity.totalQuantity,
            totalPrice = entity.totalPrice,
            status = entity.status,
            details = entity.orderDetails.map { OrderDetailResult.from(it) }
        )
    }
}