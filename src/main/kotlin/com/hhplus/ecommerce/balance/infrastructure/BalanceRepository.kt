package com.hhplus.ecommerce.balance.infrastructure

import com.hhplus.ecommerce.balance.infrastructure.exception.BalanceNotFoundException
import com.hhplus.ecommerce.balance.domain.respository.IBalanceRepository
import com.hhplus.ecommerce.balance.infrastructure.jpa.BalanceJpaRepository
import com.hhplus.ecommerce.balance.infrastructure.jpa.entity.BalanceEntity
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