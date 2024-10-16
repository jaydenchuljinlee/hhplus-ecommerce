package com.hhplus.ecommerce.api.product.dto

data class BestSellingTopFiveResponse(
    var productId: Long = 0,
    var productName: String = "",
    var stock: Int = 0,
    var totalOrderCount: Int = 0,
    var totalPayPrice: Long = 0,
    var orderCount: Long = 0,
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
    }
}