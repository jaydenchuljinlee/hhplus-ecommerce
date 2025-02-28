package com.hhplus.ecommerce.cart.api.dto

import com.hhplus.ecommerce.cart.domain.dto.CartCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

data class CartCreationRequest(
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Parameter(description = "상품 ID", required = true)
    @Schema(description = "상품 ID", example = "1")
    var productId: Long,
    @Parameter(description = "상품 수량", required = true)
    @Schema(description = "상품 수량", example = "10")
    var quantity: Int,
) {
    fun toCartCreation(): CartCreation {
        return CartCreation(userId, productId, quantity)
    }
}