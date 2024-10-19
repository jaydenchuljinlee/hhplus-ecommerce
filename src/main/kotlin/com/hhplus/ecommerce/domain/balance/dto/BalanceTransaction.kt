package com.hhplus.ecommerce.domain.balance.dto

data class BalanceTransaction(
    var userId: Long,
    var amount: Long,
    var type: TransactionType,
) {
    enum class TransactionType {
        CHARGE, USE
    }
}