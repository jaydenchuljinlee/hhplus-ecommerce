package com.hhplus.ecommerce.domain.balance

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.domain.balance.dto.BalanceQuery
import com.hhplus.ecommerce.domain.balance.dto.BalanceResult
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import com.hhplus.ecommerce.domain.balance.respository.IBalanceRepository
import com.hhplus.ecommerce.infrastructure.balance.event.BalanceEventPublisher
import com.hhplus.ecommerce.infrastructure.balance.mongodb.BalanceHistoryDocument
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BalanceService(
    private val balanceRepository: IBalanceRepository,
    private val balanceSpringEventPublisher: BalanceEventPublisher
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

    @RedisLock(key = "'balance:' + #item.userId") // 잔액 정보에 대한 사용자 ID를 기반으로 Lock을 점유한다. 1:1 매핑이라 사용자 ID로 락을 잡도록 했다.
    fun charge(item: BalanceTransaction): BalanceResult {
        val balanceEntity = balanceRepository.findByUserId(item.userId)

        balanceEntity.charge(item.amount)

        balanceRepository.insertOrUpdate(balanceEntity)

        val balanceHistoryDocument = BalanceHistoryDocument(
            balanceId = balanceEntity.id,
            amount = item.amount,
            balance = balanceEntity.balance,
            transactionType = "CHARGE"
        )

        balanceSpringEventPublisher.publish(balanceHistoryDocument)

        return BalanceResult.from(balanceEntity)

    }

    @RedisLock(key = "'balance:' + #item.userId") // 잔액 정보에 대한 사용자 ID를 기반으로 Lock을 점유한다. 1:1 매핑이라 사용자 ID로 락을 잡도록 했다.
    fun use(item: BalanceTransaction): BalanceResult {
        val balanceEntity = balanceRepository.findByUserId(item.userId)

        balanceEntity.use(item.amount)

        balanceRepository.insertOrUpdate(balanceEntity)

        val balanceHistoryDocument = BalanceHistoryDocument(
            balanceId = balanceEntity.id,
            amount = item.amount,
            balance = balanceEntity.balance,
            transactionType = "USE"
        )

        balanceSpringEventPublisher.publish(balanceHistoryDocument)

        return BalanceResult.from(balanceEntity)
    }
}