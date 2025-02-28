package com.hhplus.ecommerce.balance.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.common.properties.BalanceKafkaProperties
import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.balance.domain.respository.IBalanceRepository
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryDocument
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BalanceService(
    private val balanceRepository: IBalanceRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val balanceKafkaProperties: BalanceKafkaProperties,
    private val objectMapper: ObjectMapper,
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

        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = balanceKafkaProperties.groupId,
            topic = balanceKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(balanceHistoryDocument)
        )

        applicationEventPublisher.publishEvent(outboxEvent)

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

        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = balanceKafkaProperties.groupId,
            topic = balanceKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(balanceHistoryDocument)
        )

        applicationEventPublisher.publishEvent(outboxEvent)

        return BalanceResult.from(balanceEntity)
    }
}