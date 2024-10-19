package com.hhplus.ecommerce.domain.balance

import com.hhplus.ecommerce.domain.balance.dto.BalanceQuery
import com.hhplus.ecommerce.domain.balance.dto.BalanceResult
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import com.hhplus.ecommerce.infrastructure.balance.IBalanceHistoryRepository
import com.hhplus.ecommerce.infrastructure.balance.IBalanceRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceHistoryEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BalanceService(
    private val balanceRepository: IBalanceRepository,
    private val balanceHistoryRepository: IBalanceHistoryRepository
) {
    fun validateBalanceToUse(item: BalanceTransaction) {
        val balanceEntity = balanceRepository.findByUserId(item.userId)

        balanceEntity.validateToUse(balanceEntity.balance - item.amount)
    }

    @Transactional
    fun getBalanceWithLock(item: BalanceQuery): BalanceResult {
        val balanceEntity = balanceRepository.findByUserIdWithLock(item.userId)
        return BalanceResult.from(balanceEntity)
    }

    fun getBalance(item: BalanceQuery): BalanceResult {
        val balanceEntity = balanceRepository.findByUserId(item.userId)
        return BalanceResult.from(balanceEntity)
    }

    @Transactional
    fun charge(item: BalanceTransaction): BalanceResult {
        val balanceEntity = balanceRepository.findByUserIdWithLock(item.userId)

        balanceEntity.charge(item.amount)

        balanceRepository.insertOrUpdate(balanceEntity)

        balanceHistoryRepository.insertOrUpdate(
            BalanceHistoryEntity(
            balanceId = balanceEntity.id,
            amount = item.amount,
            balance = balanceEntity.balance,
            transactionType = "CHARGE"
            )
        )

        return BalanceResult.from(balanceEntity)
    }

    @Transactional
    fun use(item: BalanceTransaction): BalanceResult {
        val balanceEntity = balanceRepository.findByUserIdWithLock(item.userId)

        balanceEntity.use(item.amount)

        val entity = balanceRepository.insertOrUpdate(balanceEntity)

        balanceHistoryRepository.insertOrUpdate(
            BalanceHistoryEntity(
                balanceId = balanceEntity.id,
                amount = item.amount,
                balance = balanceEntity.balance,
                transactionType = "USE"
            )
        )

        return BalanceResult.from(balanceEntity)
    }
}