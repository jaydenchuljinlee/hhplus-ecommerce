package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

data class OrderCreationCommand(
    var userId: Long,
    var details: List<OrderDetailCreationCommand>
) {
    fun toEntity(): OrderEntity {

        return OrderEntity(
            userId = userId,
            totalPrice = details.map { it.quantity * it.price }.sum(),
            totalQuantity = details.map { it.quantity }.sum(),
            status = OrderStatus.REQUESTED
        )
    }
}