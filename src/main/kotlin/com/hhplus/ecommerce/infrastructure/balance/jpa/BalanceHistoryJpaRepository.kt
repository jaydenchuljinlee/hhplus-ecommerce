package com.hhplus.ecommerce.infrastructure.balance.jpa

import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceHistoryJpaRepository: JpaRepository<BalanceHistoryEntity, Long> {
    fun findByBalanceId(balanceId: Long): List<BalanceHistoryEntity>
}