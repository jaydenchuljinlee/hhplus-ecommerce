package com.hhplus.ecommerce.usercase.order.dto

class OrderInfo(
    var orderId: Long,
    var userId: Long,
    var productId: Long,
    var quantity: Int,
    var price: Long,
)