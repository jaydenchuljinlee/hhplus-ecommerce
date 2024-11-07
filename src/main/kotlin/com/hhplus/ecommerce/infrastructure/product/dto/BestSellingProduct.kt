package com.hhplus.ecommerce.infrastructure.product.dto

import java.io.Serializable

data class BestSellingProduct(
    var productId: Long = 0,
    var productName: String = "",
    var stock: Int = 0,
    var totalOrderCount: Int = 0,
    var totalPayPrice: Long = 0,
    var orderCount: Long = 0,
    var payCount: Long = 0,
): Serializable