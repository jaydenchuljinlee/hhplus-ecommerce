package com.hhplus.ecommerce.api.balance.dto

data class BalanceTransactionResponse(
    var userId: Long,
    var amount: Long,
    var transactionType: String,
) {
    companion object {
        fun getInstance() = BalanceTransactionResponse(
            userId = 0L,
            amount = 1000L,
            transactionType = "Charge"
        )
    }
}