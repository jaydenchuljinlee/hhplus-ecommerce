package com.hhplus.ecommerce.domain.balance.dto

import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceEntity

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