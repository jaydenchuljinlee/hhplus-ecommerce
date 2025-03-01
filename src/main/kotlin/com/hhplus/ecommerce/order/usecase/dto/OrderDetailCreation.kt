package com.hhplus.ecommerce.order.usecase.dto

import com.hhplus.ecommerce.order.domain.dto.OrderDetailCreationCommand
import com.hhplus.ecommerce.product.domain.dto.ProductDetailQuery

data class OrderDetailCreation(
    var productId: Long,
    var quantity: Int,
    var price: Long,
) {
    fun toProductDetailQuery(): ProductDetailQuery {
        return ProductDetailQuery(
            productId = productId,
        )
    }

    fun toDetailCreationCommand(): OrderDetailCreationCommand {
        return OrderDetailCreationCommand(
            productId = productId,
            quantity = quantity,
            price = price,
        )
    }
}