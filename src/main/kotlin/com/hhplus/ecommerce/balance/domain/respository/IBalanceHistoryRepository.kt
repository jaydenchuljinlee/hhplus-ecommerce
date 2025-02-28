package com.hhplus.ecommerce.balance.domain.respository

import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceHistoryEntity

interface IBalanceHistoryRepository {
    fun findAllByBalanceId(userId: Long): List<BalanceHistoryEntity>
    fun insertOrUpdate(entity: BalanceHistoryEntity): BalanceHistoryEntity
}