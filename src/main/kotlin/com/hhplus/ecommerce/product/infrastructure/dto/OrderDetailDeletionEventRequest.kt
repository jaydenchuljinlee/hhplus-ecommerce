package com.hhplus.ecommerce.product.infrastructure.dto

data class OrderDetailDeletionEventRequest(
    var orderId: Long,
    var productId: Long
) {
    companion object {
        fun of(orderId: Long, productId: Long): OrderDetailDeletionEventRequest {
            return OrderDetailDeletionEventRequest(
                orderId, productId
            )
        }
    }
}