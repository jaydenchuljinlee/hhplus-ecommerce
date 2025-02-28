package com.hhplus.ecommerce.balance.domain.dto

import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceEntity

data class BalanceResult(
    var userId: Long,
    var balance: Long
) {
    companion object {
        fun from(entity: BalanceEntity): BalanceResult {
            return BalanceResult(
                userId = entity.userId,
                balance = entity.balance,
            )
        }
    }
}