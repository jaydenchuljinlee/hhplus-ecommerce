package com.hhplus.ecommerce.balance.domain

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.balance.domain.event.IBalanceEventPublisher
import com.hhplus.ecommerce.balance.domain.respository.IBalanceRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BalanceService(
    private val balanceRepository: IBalanceRepository,
    private val balanceEventPublisher: IBalanceEventPublisher,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(BalanceService::class.java)
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

    @RedisLock(key = "'balance:' + #item.userId")
    fun charge(item: BalanceTransaction): BalanceResult {
        val balanceEntity = balanceRepository.findByUserId(item.userId)

        balanceEntity.charge(item.amount)
        balanceRepository.insertOrUpdate(balanceEntity)

        balanceEventPublisher.publishCharge(
            balanceId = balanceEntity.id,
            amount = item.amount,
            balance = balanceEntity.balance
        )

        return BalanceResult.from(balanceEntity)
    }

    @RedisLock(key = "'balance:' + #item.userId")
    fun use(item: BalanceTransaction): BalanceResult {
        val balanceEntity = balanceRepository.findByUserId(item.userId)

        balanceEntity.use(item.amount)
        balanceRepository.insertOrUpdate(balanceEntity)

        logger.debug("잔액 정보 => {}", balanceEntity)

        balanceEventPublisher.publishUse(
            balanceId = balanceEntity.id,
            amount = item.amount,
            balance = balanceEntity.balance
        )

        return BalanceResult.from(balanceEntity)
    }
}
