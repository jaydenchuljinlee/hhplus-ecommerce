package com.hhplus.ecommerce.domain.product.dto

data class ProductInfoResult(
    var productId: Long,
    var productName: String,
    var price: Long,
    var stock: Int
)