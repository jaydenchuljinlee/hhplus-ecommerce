package com.hhplus.ecommerce.api.order.dto

class OrderResponse {
    data class Preparation(
        var orderId: Long,
        var productId: Long,
        var price: Int,
        var quantity: Int,
        var status: String,
    ) {
        companion object {
            fun getInstance() = Preparation(
                orderId = 0L,
                productId = 0L,
                price = 1000,
                quantity = 2,
                status = "ORDER_COMPLETED"
            )
        }
    }
}