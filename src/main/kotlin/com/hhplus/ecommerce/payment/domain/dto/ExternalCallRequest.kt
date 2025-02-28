package com.hhplus.ecommerce.payment.domain.dto

data class ExternalCallRequest(
    var payId: Long,
    var userId: Long,
    var price: Long,
)