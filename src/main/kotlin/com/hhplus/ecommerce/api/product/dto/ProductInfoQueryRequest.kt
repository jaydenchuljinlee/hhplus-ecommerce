package com.hhplus.ecommerce.api.product.dto

import com.hhplus.ecommerce.domain.product.dto.ProductInfoQuery

data class ProductInfoQueryRequest(
    var productId: Long
) {
    fun toProductInfoQuery(): ProductInfoQuery = ProductInfoQuery(productId)
}