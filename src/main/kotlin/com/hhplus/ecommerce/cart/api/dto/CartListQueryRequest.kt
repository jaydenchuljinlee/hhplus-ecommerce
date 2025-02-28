package com.hhplus.ecommerce.cart.api.dto

import com.hhplus.ecommerce.cart.domain.dto.CartListQuery

data class CartListQueryRequest(
    var userId: Long
) {
    fun toCartListQuery(): CartListQuery {
        return CartListQuery(userId)
    }
}