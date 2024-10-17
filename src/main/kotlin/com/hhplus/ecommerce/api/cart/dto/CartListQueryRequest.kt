package com.hhplus.ecommerce.api.cart.dto

import com.hhplus.ecommerce.domain.cart.dto.CartListQuery

data class CartListQueryRequest(
    var userId: Long
) {
    fun toCartListQuery(): CartListQuery {
        return CartListQuery(userId)
    }
}