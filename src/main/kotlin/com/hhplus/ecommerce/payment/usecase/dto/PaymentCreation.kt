package com.hhplus.ecommerce.payment.usecase.dto

data class PaymentCreation(
    var orderId: Long,
    var userId: Long
)