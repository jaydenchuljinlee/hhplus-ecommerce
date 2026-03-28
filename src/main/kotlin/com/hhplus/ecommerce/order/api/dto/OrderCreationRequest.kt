package com.hhplus.ecommerce.order.api.dto

import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty

class OrderCreationRequest(
    @field:Min(1, message = "userId는 1 이상이어야 합니다.")
    @Schema(description = "사용자 ID", example = "1")
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
    @field:NotEmpty(message = "주문 품목은 1개 이상이어야 합니다.")
    @field:Valid
    @Schema(description = "주문 품목 목록")
    @Parameter(description = "주문 품목 목록", required = true)
    var details: List<DetailCreationRequest>
) {
    data class DetailCreationRequest(
        @field:Min(1, message = "productId는 1 이상이어야 합니다.")
        @Schema(description = "상품 ID", example = "1")
        @Parameter(description = "상품 ID", required = true)
        var productId: Long,
        @field:Min(1, message = "quantity는 1 이상이어야 합니다.")
        @Schema(description = "주문 수량", example = "10")
        @Parameter(description = "주문 수량", required = true)
        var quantity: Int,
        @field:Min(0, message = "price는 0 이상이어야 합니다.")
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
