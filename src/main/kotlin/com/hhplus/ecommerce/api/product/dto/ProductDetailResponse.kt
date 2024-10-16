package com.hhplus.ecommerce.api.product.dto


data class ProductDetailResponse(
    var productId: Long,
    var name: String,
    var description: String,
    var price: Long,
    var imageUrl: String,
    var quantity: Int,
) {
    companion object {
        fun getInstance(): ProductDetailResponse = ProductDetailResponse(
            productId = 0L,
            name = "한우 세트",
            description = "맛있는 한우",
            price = 150_000L,
            imageUrl = "https://thumbnails",
            quantity = 10
        )
    }
}