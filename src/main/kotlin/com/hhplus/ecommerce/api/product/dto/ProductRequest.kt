package com.hhplus.ecommerce.api.product.dto

import io.swagger.v3.oas.annotations.Parameter

class ProductViewRequest(
    @Parameter(description = "상품 ID", required = true)
    var productId: Long,
) {

}