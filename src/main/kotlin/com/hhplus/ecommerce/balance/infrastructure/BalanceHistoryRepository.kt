package com.hhplus.ecommerce.balance.infrastructure

import com.hhplus.ecommerce.balance.domain.respository.IBalanceHistoryRepository
import com.hhplus.ecommerce.balance.infrastructure.jpa.BalanceHistoryJpaRepository
import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceHistoryEntity
import org.springframework.stereotype.Repository

@Repository
class BalanceHistoryRepository(
    private val balanceHistoryJpaRepository: BalanceHistoryJpaRepository
): IBalanceHistoryRepository {
    override fun findAllByBalanceId(balanceId: Long): List<BalanceHistoryEntity> {
        return balanceHistoryJpaRepository.findByBalanceId(balanceId)
    }

    override fun insertOrUpdate(entity: BalanceHistoryEntity): BalanceHistoryEntity {
        return balanceHistoryJpaRepository.save(entity)
    }
}