package com.hhplus.ecommerce.cart.api.dto

import com.hhplus.ecommerce.cart.domain.dto.CartListQuery
import jakarta.validation.constraints.Min

data class CartListQueryRequest(
    @field:Min(1) var userId: Long
) {
    fun toCartListQuery(): CartListQuery {
        return CartListQuery(userId)
    }
}