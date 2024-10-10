package com.hhplus.ecommerce.api.balance.dto

class BalanceRequest {
    data class Charge(
        var userId: Long,
        var amount: Int,
    )

    data class View(
        var userId: Long,
    )
}