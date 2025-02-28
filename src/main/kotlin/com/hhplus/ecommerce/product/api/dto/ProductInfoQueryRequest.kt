package com.hhplus.ecommerce.product.api.dto

import com.hhplus.ecommerce.product.domain.dto.ProductInfoQuery

data class ProductInfoQueryRequest(
    var productId: Long
) {
    fun toProductInfoQuery(): ProductInfoQuery = ProductInfoQuery(productId)
}