package com.hhplus.ecommerce.api.order.dto

import io.swagger.v3.oas.annotations.Parameter

class OrderCreationRequest(
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
    @Parameter(description = "상품 ID", required = true)
    var productId: Long,
    @Parameter(description = "주문 수량", required = true)
    var quantity: Int,
    @Parameter(description = "주문 가격", required = true)
    var price: Int,
)