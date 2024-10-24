package com.hhplus.ecommerce.infrastructure.balance

import com.hhplus.ecommerce.domain.balance.respository.IBalanceHistoryRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.BalanceHistoryJpaRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceHistoryEntity
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