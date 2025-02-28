package com.hhplus.ecommerce.product.domain.dto

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity

data class ProductDetailResult(
    var productId: Long,
    var productDetailId: Long,
    var quantity: Int
) {
    companion object {
        fun from(entity: ProductDetailEntity) = ProductDetailResult(
            productId = entity.productId,
            productDetailId = entity.id,
            quantity = entity.quantity
        )
    }
}