package com.hhplus.ecommerce.api.balance.dto

class BalanceViewResponse(
    var userId: Long,
    var balance: Long
) {
    companion object {
        fun getInstance() = BalanceViewResponse(
            userId = 0L,
            balance = 1000L
        )
    }
}