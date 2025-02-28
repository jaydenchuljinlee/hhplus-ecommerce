package com.hhplus.ecommerce.product.domain.dto

import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException

data class DecreaseProductDetailStock(
    var id: Long,
    var amount: Int,
    var stock: Int,
) {
    init {
        require(stock - amount >= 0) { OutOfStockException() }
    }
}