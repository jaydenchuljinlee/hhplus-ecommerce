package com.hhplus.ecommerce.order.api.dto

import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

class OrderCreationRequest(
    @Schema(description = "사용자 ID", example = "1")
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
    @Schema(description = "주문 품목 목록")
    @Parameter(description = "주문 품목 목록", required = true)
    var details: List<DetailCreationRequest>
) {
    data class DetailCreationRequest(
        @Schema(description = "상품 ID", example = "1")
        @Parameter(description = "상품 ID", required = true)
        var productId: Long,
        @Schema(description = "주문 수량", example = "10")
        @Parameter(description = "주문 수량", required = true)
        var quantity: Int,
        @Schema(description = "주문 가격", example = "1000")
        @Parameter(description = "주문 가격", required = true)
        var price: Long,
    ) {
        fun toDetailCreation(): OrderDetailCreation {
            return OrderDetailCreation(
                productId = productId,
                quantity = quantity,
                price = price,
            )
        }
    }

    fun toOrderCreation(): OrderCreation {
        return OrderCreation.from(
            userId = userId,
            details = details.map { it.toDetailCreation() }
        )
    }
}