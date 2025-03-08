package com.hhplus.ecommerce.product.domain.dto

import com.hhplus.ecommerce.product.infrastructure.dto.OrderProductStockEventResponse

data class DecreaseProductDetailStock(
    var id: Long,
    var amount: Int,
) {
    companion object {
        fun of(dto: OrderProductStockEventResponse.ProductInfo): DecreaseProductDetailStock {
            return DecreaseProductDetailStock(
                id = dto.productId,
                amount = dto.quantity
            )
        }
    }
}