package com.hhplus.ecommerce.order.domain.dto

import com.hhplus.ecommerce.order.common.OrderStatus

data class OrderQuery(
    var orderId: Long,
    var status: OrderStatus
)