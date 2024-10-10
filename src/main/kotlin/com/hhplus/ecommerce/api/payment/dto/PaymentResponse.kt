package com.hhplus.ecommerce.api.payment.dto

class PaymentResponse {
    data class Payment(
        var id: Long,
        var userId: Long,
        var orderId: Long,
        var productId: Long,
        var quantity: Int,
        var price: Int,
        var status: String,
    ) {
        companion object {
            fun getInstance() = Payment(
                id = 0L,
                userId = 0L,
                orderId = 0L,
                productId = 0L,
                quantity = 0,
                price = 0,
                status = "PAYMENT_COMPLETED"
            )
        }
    }

}