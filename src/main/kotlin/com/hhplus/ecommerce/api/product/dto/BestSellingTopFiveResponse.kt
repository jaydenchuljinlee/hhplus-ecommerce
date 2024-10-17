package com.hhplus.ecommerce.api.product.dto

import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import io.swagger.v3.oas.annotations.media.Schema

data class BestSellingTopFiveResponse(
    @Schema(description = "상품 ID", example = "1")
    var productId: Long = 0,
    @Schema(description = "상품 이름", example = "1")
    var productName: String = "",
    @Schema(description = "상품 재고", example = "1")
    var stock: Int = 0,
    @Schema(description = "전체 주문 수량", example = "1")
    var totalOrderCount: Int = 0,
    @Schema(description = "전체 결제 금액", example = "1")
    var totalPayPrice: Long = 0,
    @Schema(description = "주문 수량", example = "1")
    var orderCount: Long = 0,
    @Schema(description = "결제 수량", example = "1")
    var payCount: Long = 0,
) {
    companion object {
        fun getInstance() = BestSellingTopFiveResponse(
            productId = 0,
            productName = "",
            stock = 0,
            totalOrderCount = 0,
            totalPayPrice = 0,
            orderCount = 0,
            payCount = 0
        )

        fun from(dto: BestSellingProduct): BestSellingTopFiveResponse {
            return BestSellingTopFiveResponse(
                productId = dto.productId,
                productName = dto.productName,
                stock = dto.stock,
                totalOrderCount = dto.totalOrderCount,
                totalPayPrice = dto.totalPayPrice,
                orderCount = dto.orderCount,
                payCount = dto.payCount
            )
        }

    }
}