package com.hhplus.ecommerce.balance.domain.dto

data class BalanceTransaction(
    var userId: Long,
    var amount: Long,
    var type: TransactionType,
) {
    enum class TransactionType {
        CHARGE, USE
    }
}