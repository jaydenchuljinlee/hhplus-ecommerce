package com.hhplus.ecommerce.domain.product.dto

import com.hhplus.ecommerce.infrastructure.product.exception.OutOfStockException

data class DecreaseProductDetailStock(
    var id: Long,
    var amount: Int,
    var stock: Int,
) {
    init {
        require(stock - amount >= 0) { OutOfStockException() }
    }
}