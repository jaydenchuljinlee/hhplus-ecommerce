package com.hhplus.ecommerce.balance.infrastructure.jpa

import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface BalanceJpaRepository: JpaRepository<BalanceEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT b FROM BalanceEntity b WHERE b.userId = :userId")
    fun findByUserIdWithLock(@Param("userId") userId: Long): Optional<BalanceEntity>

    fun findByUserId(userId: Long): Optional<BalanceEntity>
}