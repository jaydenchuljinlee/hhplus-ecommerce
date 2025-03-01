package com.hhplus.ecommerce.order.api.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
import io.swagger.v3.oas.annotations.media.Schema

class OrderResponse(
    @Schema(description = "주문 ID", example = "1")
    var orderId: Long,
    @Schema(description = "주문 상태", example = "ORDER_COMPLETED")
    var status: OrderStatus,
    @Schema(description = "품목")
    var details: List<Detail> = emptyList(),
) {
    companion object {
        fun from(info: OrderInfo): OrderResponse {
            return OrderResponse(
                orderId = info.orderId,
                status = info.status,
                details = info.details.map { Detail.from(it) }
            )
        }
    }

    data class Detail(
        @Schema(description = "상품 ID", example = "1")
        var productId: Long,
        @Schema(description = "주문 금액", example = "1000")
        var price: Long,
        @Schema(description = "주문 수량", example = "10")
        var quantity: Int,
    ) {
        companion object {
            fun from(detail: OrderInfo.DetailInfo): Detail {
                return Detail(
                    productId = detail.productId,
                    price = detail.price,
                    quantity = detail.quantity
                )
            }
        }
    }
}