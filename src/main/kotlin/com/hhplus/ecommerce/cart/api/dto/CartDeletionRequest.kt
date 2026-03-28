package com.hhplus.ecommerce.cart.api.dto

import com.hhplus.ecommerce.cart.domain.dto.CartDeletion
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class CartDeletionRequest(
    @field:Min(1, message = "cartId는 1 이상이어야 합니다.")
    @Parameter(description = "장바구니 ID", required = true)
    @Schema(description = "장바구니 ID", example = "1")
    var cartId: Long,
) {
    fun toCartDeletion(): CartDeletion {
        return CartDeletion(cartId)
    }
}