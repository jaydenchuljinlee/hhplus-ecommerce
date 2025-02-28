package com.hhplus.ecommerce.product.domain.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ProductInfoResult  @JsonCreator constructor(
    @JsonProperty("productId") var productId: Long,
    @JsonProperty("productName") var productName: String,
    @JsonProperty("price") var price: Long,
    @JsonProperty("stock") var stock: Int
): Serializable