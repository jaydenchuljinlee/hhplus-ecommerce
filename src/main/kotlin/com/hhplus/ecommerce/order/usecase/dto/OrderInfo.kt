package com.hhplus.ecommerce.order.usecase.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.dto.OrderDetailResult
import com.hhplus.ecommerce.order.domain.dto.OrderResult

class OrderInfo(
    var orderId: Long,
    var userId: Long,
    var status: OrderStatus,
    var details: List<DetailInfo> = emptyList()
) {
    companion object {
        fun from(order: OrderResult) = OrderInfo(
            orderId = order.orderId,
            userId = order.userId,
            status = order.status,
            details = order.details.map { DetailInfo.from(it) }
        )
    }

    data class DetailInfo(
        var productId: Long,
        var quantity: Int,
        var price: Long,
    ) {
        companion object {
            fun from(detail: OrderDetailResult) = DetailInfo(
                productId = detail.productId,
                quantity = detail.quantity,
                price = detail.price,
            )
        }
    }
}