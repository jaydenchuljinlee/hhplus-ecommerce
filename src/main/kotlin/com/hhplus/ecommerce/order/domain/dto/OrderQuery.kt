package com.hhplus.ecommerce.order.domain.dto

data class OrderQuery(
    var orderId: Long,
    var status: String
)