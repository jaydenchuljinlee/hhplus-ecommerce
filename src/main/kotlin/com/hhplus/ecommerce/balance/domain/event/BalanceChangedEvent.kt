package com.hhplus.ecommerce.balance.domain.event

data class BalanceChangedEvent(
    val balanceId: Long,
    val amount: Long,
    val balance: Long,
    val transactionType: String
)
