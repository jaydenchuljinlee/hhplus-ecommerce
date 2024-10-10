package com.hhplus.ecommerce.api.order.dto

class OrderRequest {
    data class Preparation(
        var userId: Long,
        var productId: Long,
        var quantity: Int,
        var price: Int,
    )
}