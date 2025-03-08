package com.hhplus.ecommerce.product.infrastructure.dto

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