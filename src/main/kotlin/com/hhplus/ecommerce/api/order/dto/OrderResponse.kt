package com.hhplus.ecommerce.api.order.dto

import com.hhplus.ecommerce.usercase.order.dto.OrderInfo
import io.swagger.v3.oas.annotations.media.Schema

class OrderResponse(
    @Schema(description = "주문 ID", example = "1")
    var orderId: Long,
    @Schema(description = "상품 ID", example = "1")
    var productId: Long,
    @Schema(description = "주문 금액", example = "1000")
    var price: Long,
    @Schema(description = "주문 수량", example = "10")
    var quantity: Int,
    @Schema(description = "주문 상태", example = "ORDER_COMPLETED")
    var status: String,
) {
    companion object {
        fun from(info: OrderInfo): OrderResponse {
            return OrderResponse(
                orderId = info.orderId,
                productId = info.productId,
                price = info.price,
                quantity = info.quantity,
                status = info.status
            )
        }
    }
}