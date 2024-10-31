package com.hhplus.ecommerce.domain.balance

import com.hhplus.ecommerce.domain.balance.dto.BalanceQuery
import com.hhplus.ecommerce.domain.balance.dto.BalanceResult
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import com.hhplus.ecommerce.domain.balance.respository.IBalanceHistoryRepository
import com.hhplus.ecommerce.domain.balance.respository.IBalanceRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.entity.BalanceHistoryEntity
import com.hhplus.ecommerce.infrastructure.redis.PubSubLockSupporter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class BalanceService(
    private val balanceRepository: IBalanceRepository,
    private val balanceHistoryRepository: IBalanceHistoryRepository,
    private val pubSubLockSupporter: PubSubLockSupporter
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

    fun charge(item: BalanceTransaction): BalanceResult {
        val balanceKey = "balance:${item.userId}"

        val lock = pubSubLockSupporter.getLock(balanceKey)

        try {
            val acquireLock = lock.tryLock(10, 1, TimeUnit.SECONDS)

            if (!acquireLock) {
                throw InterruptedException("Lock 획득 실패")
            }

            val balanceEntity = balanceRepository.findByUserId(item.userId)

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
        } finally {
            lock.unlock()
        }
    }

    fun use(item: BalanceTransaction): BalanceResult {
        val balanceKey = "balance:${item.userId}"

        val lock = pubSubLockSupporter.getLock(balanceKey)

        try {
            val acquireLock = lock.tryLock(10, 1, TimeUnit.SECONDS)

            if (!acquireLock) {
                throw InterruptedException("Lock 획득 실패")
            }

            val balanceEntity = balanceRepository.findByUserId(item.userId)

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
        } finally {
            lock.unlock()
        }
    }
}