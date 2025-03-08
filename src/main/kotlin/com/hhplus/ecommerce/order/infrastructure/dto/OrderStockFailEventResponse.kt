package com.hhplus.ecommerce.order.infrastructure.dto

import com.hhplus.ecommerce.order.domain.dto.OrderDeletionCommand

data class OrderStockFailEventResponse(
    var orderId: Long,
    var productId: Long
) {
    fun toOrderDeletionCommand(): OrderDeletionCommand {
        return OrderDeletionCommand(
            orderId = orderId,
            productId = productId
        )
    }
}