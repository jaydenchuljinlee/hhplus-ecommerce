package com.hhplus.ecommerce.api.balance.dto

class BalanceResponse {
    data class Charge(
        var userId: Long,
        var amount: Long,
        var transactionType: String,
    ) {
        companion object {
            fun getInstance() = Charge(
                userId = 0L,
                amount = 1000L,
                transactionType = "Charge"
            )
        }
    }

    data class View(
        var userId: Long,
        var balance: Long
    ) {
        companion object {
            fun getInstance() = View(
                userId = 0L,
                balance = 1000L
            )
        }
    }
}