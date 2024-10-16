package com.hhplus.ecommerce.domain.payment.dto

data class ExternalCallRequest(
    var payId: Long,
    var userId: Long,
    var price: Long,
)