package com.hhplus.ecommerce.infrastructure.balance

import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceHistoryEntity

interface IBalanceHistoryRepository {
    fun findAllByBalanceId(userId: Long): List<BalanceHistoryEntity>
    fun insertOrUpdate(entity: BalanceHistoryEntity): BalanceHistoryEntity
}