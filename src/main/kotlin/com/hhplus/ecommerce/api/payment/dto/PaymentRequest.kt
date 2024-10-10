package com.hhplus.ecommerce.api.payment.dto

class PaymentRequest {
    data class Payment(
        var orderId: Long,
    ) {
        companion object {
            fun getInstance() = Payment(
                orderId = 0L,
            )
        }
    }
}