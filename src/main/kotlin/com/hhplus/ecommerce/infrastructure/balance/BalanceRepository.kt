package com.hhplus.ecommerce.infrastructure.balance

import com.hhplus.ecommerce.infrastructure.balance.exception.BalanceNotFoundException
import com.hhplus.ecommerce.domain.balance.respository.IBalanceRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.BalanceJpaRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceEntity
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