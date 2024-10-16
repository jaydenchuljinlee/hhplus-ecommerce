package com.hhplus.ecommerce.domain.order.dto

data class OrderQuery(
    var orderId: Long,
    var status: String
)