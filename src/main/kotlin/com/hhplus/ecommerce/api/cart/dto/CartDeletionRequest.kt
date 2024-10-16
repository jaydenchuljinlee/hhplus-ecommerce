package com.hhplus.ecommerce.api.cart.dto

import io.swagger.v3.oas.annotations.Parameter

data class CartDeletionRequest(
    @Parameter(description = "장바구니 ID", required = true)
    var cartId: Long,
)