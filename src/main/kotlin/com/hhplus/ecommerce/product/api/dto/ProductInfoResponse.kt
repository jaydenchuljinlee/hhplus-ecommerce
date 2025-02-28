package com.hhplus.ecommerce.product.api.dto

import com.hhplus.ecommerce.product.domain.dto.ProductInfoResult
import io.swagger.v3.oas.annotations.media.Schema


data class ProductInfoResponse(
    @Schema(description = "상품 ID", example = "1")
    var productId: Long,
    @Schema(description = "상품 이름", example = "1")
    var productName: String,
    @Schema(description = "상품 가격", example = "1")
    var price: Long,
    @Schema(description = "상품 수량", example = "1")
    var quantity: Int,
) {
    companion object {
        fun from(info: ProductInfoResult): ProductInfoResponse {
            return ProductInfoResponse(
                productId = info.productId,
                productName = info.productName,
                price = info.price,
                quantity = info.stock
            )
        }
    }
}