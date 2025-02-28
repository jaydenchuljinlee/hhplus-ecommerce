package com.hhplus.ecommerce.payment.domain.dto

data class CreationPaymentCommand(
    var orderId: Long,
    var userId: Long,
    var price: Long
)