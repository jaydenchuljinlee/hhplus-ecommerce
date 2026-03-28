package com.hhplus.ecommerce.cart.api.dto

import com.hhplus.ecommerce.cart.domain.dto.CartCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class CartCreationRequest(
    @field:Min(1, message = "userId는 1 이상이어야 합니다.")
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @field:Min(1, message = "productId는 1 이상이어야 합니다.")
    @Parameter(description = "상품 ID", required = true)
    @Schema(description = "상품 ID", example = "1")
    var productId: Long,
    @field:Min(1, message = "quantity는 1 이상이어야 합니다.")
    @Parameter(description = "상품 수량", required = true)
    @Schema(description = "상품 수량", example = "10")
    var quantity: Int,
) {
    fun toCartCreation(): CartCreation {
        return CartCreation(userId, productId, quantity)
    }
}