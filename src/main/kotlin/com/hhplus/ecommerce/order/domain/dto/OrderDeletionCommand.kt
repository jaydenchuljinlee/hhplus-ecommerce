package com.hhplus.ecommerce.order.domain.dto

data class OrderDeletionCommand(
    var orderId: Long,
    var productId: Long
)