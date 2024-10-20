package com.hhplus.ecommerce.domain.balance.respository

import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceHistoryEntity

interface IBalanceHistoryRepository {
    fun findAllByBalanceId(userId: Long): List<BalanceHistoryEntity>
    fun insertOrUpdate(entity: BalanceHistoryEntity): BalanceHistoryEntity
}