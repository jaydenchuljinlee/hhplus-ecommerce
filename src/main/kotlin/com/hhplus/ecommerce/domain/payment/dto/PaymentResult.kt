package com.hhplus.ecommerce.domain.payment.dto


data class PaymentResult(
    var paymentId: Long,
    var userId: Long,
    var orderId: Long,
    var status: String,
    var price: Long,
)