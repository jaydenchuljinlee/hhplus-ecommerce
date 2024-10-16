package com.hhplus.ecommerce.api.product.dto

class ProductResponse {
    data class Detail(
        var productId: Long,
        var name: String,
        var description: String,
        var price: Long,
        var imageUrl: String,
        var quantity: Int,
    ) {
        companion object {
            fun getInstance(): Detail = Detail(
                productId = 0L,
                name = "한우 세트",
                description = "맛있는 한우",
                price = 150_000L,
                imageUrl = "https://thumbnails",
                quantity = 10
            )
        }
    }

    data class TopFiveResult(
        var products: List<Detail>
    ) {
        companion object {
            fun getInstance(): TopFiveResult {
                val list = mutableListOf<Detail>()

                (1..5).forEach {
                    val detail = Detail.getInstance()
                    detail.productId += it
                    list.add(detail)
                }

                return TopFiveResult(
                    products = list
                )
            }
        }
    }
}