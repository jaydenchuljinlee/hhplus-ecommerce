package com.hhplus.ecommerce.domain.payment.dto

data class CreationPaymentCommand(
    var orderId: Long,
    var userId: Long,
    var price: Long
)