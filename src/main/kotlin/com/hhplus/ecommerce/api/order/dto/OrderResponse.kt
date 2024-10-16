package com.hhplus.ecommerce.api.order.dto

class OrderResponse(
    var orderId: Long,
    var productId: Long,
    var price: Int,
    var quantity: Int,
    var status: String,
) {
    companion object {
        fun getInstance() = OrderResponse(
            orderId = 0L,
            productId = 0L,
            price = 1000,
            quantity = 2,
            status = "ORDER_COMPLETED"
        )
    }
}