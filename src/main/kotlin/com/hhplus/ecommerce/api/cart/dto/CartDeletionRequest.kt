package com.hhplus.ecommerce.api.cart.dto

import com.hhplus.ecommerce.domain.cart.dto.CartDeletion
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

data class CartDeletionRequest(
    @Parameter(description = "장바구니 ID", required = true)
    @Schema(description = "장바구니 ID", example = "1")
    var cartId: Long,
) {
    fun toCartDeletion(): CartDeletion {
        return CartDeletion(cartId)
    }
}