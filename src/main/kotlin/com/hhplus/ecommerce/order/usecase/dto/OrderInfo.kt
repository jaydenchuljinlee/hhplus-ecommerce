package com.hhplus.ecommerce.order.usecase.dto

import com.hhplus.ecommerce.order.common.OrderStatus

class OrderInfo(
    var orderId: Long,
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    var price: Long,
    var status: OrderStatus,
)