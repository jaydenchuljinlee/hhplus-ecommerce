package com.hhplus.ecommerce.balance.infrastructure.jpa

import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceHistoryJpaRepository: JpaRepository<BalanceHistoryEntity, Long> {
    fun findByBalanceId(balanceId: Long): List<BalanceHistoryEntity>
}