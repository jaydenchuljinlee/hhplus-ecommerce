package com.hhplus.ecommerce.api.cart.dto

import com.hhplus.ecommerce.domain.cart.dto.CartResult
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

class CartResponse(
    @Parameter(description = "장바구니 ID", required = true)
    @Schema(description = "장바구니 ID", example = "1")
    var cartId: Long,
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Parameter(description = "상품 ID", required = true)
    @Schema(description = "상품 ID", example = "1")
    var productId: Long,
    @Schema(description = "상품 수량", example = "10")
    var quantity: Int,
) {
    companion object {
        fun getInstance() = CartResponse(
            cartId = 0L,
            userId = 0L,
            productId = 0L,
            quantity = 10,
        )

        fun toCartResponse(result: CartResult): CartResponse {
            return CartResponse(
                cartId = result.cartId,
                userId = result.userId,
                productId = result.productId,
                quantity = result.quantity
            )
        }
    }
}