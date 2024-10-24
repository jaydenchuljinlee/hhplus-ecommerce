package com.hhplus.ecommerce.infrastructure.balance

import com.hhplus.ecommerce.common.exception.balance.BalanceNotFoundException
import com.hhplus.ecommerce.infrastructure.balance.jpa.BalanceJpaRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository

@Repository
class BalanceRepository(
    private val balanceJapRepository: BalanceJpaRepository
): IBalanceRepository {

    override fun findByUserIdWithLock(userId: Long): BalanceEntity {
        return balanceJapRepository.findByUserIdWithLock(userId).orElseThrow { BalanceNotFoundException() }
    }


    override fun findByUserId(userId: Long): BalanceEntity {
        return balanceJapRepository.findByUserId(userId).orElseThrow { BalanceNotFoundException() }
    }

    override fun insertOrUpdate(entity: BalanceEntity): BalanceEntity {
        return balanceJapRepository.save(entity)
    }
}