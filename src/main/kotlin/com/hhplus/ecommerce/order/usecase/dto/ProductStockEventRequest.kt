package com.hhplus.ecommerce.order.usecase.dto

import com.hhplus.ecommerce.order.domain.dto.OrderResult

data class ProductStockEventRequest(
    var orderId: Long,
    var userId: Long,
    var products: List<ProductInfo> = emptyList()
) {
    companion object {
        fun of (dto: OrderInfo): ProductStockEventRequest {
            return ProductStockEventRequest(
                orderId = dto.orderId,
                userId = dto.userId,
                products = dto.details.map { ProductInfo.of(it) }.toList()
            )
        }
    }

    data class ProductInfo(
        var productId: Long,
        var quantity: Int
    ) {
        companion object {
            fun of(dto: OrderInfo.DetailInfo): ProductInfo {
                return ProductInfo(
                    productId = dto.productId,
                    quantity = dto.quantity
                )
            }
        }
    }
}