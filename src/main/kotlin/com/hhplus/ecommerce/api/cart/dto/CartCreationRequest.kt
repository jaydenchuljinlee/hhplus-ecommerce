package com.hhplus.ecommerce.api.cart.dto

import io.swagger.v3.oas.annotations.Parameter

data class CartCreationRequest(
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
    @Parameter(description = "상품 ID", required = true)
    var productId: Long,
)