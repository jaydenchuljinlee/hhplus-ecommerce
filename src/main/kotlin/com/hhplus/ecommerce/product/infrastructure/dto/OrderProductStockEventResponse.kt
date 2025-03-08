package com.hhplus.ecommerce.product.infrastructure.dto

import com.hhplus.ecommerce.order.domain.dto.OrderDeletionCommand

data class OrderProductStockEventResponse(
    var orderId: Long,
    var userId: Long,
    var products: List<ProductInfo> = emptyList()
) {
    data class ProductInfo(
        var productId: Long,
        var quantity: Int
    )
}