package com.hhplus.ecommerce.balance.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.balance.domain.event.IBalanceEventPublisher
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryDocument
import com.hhplus.ecommerce.common.properties.BalanceKafkaProperties
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 잔액 이벤트 발행 구현체 (Infrastructure)
 *
 * [IBalanceEventPublisher]의 Infrastructure 구현체.
 * Outbox 패턴으로 잔액 변경 이력을 Kafka를 통해 MongoDB에 전달한다.
 *
 * @TransactionalEventListener(BEFORE_COMMIT) 이 활성화된 상태에서 호출되므로,
 * 반드시 활성 트랜잭션 내에서 호출되어야 한다.
 */
@Component
class BalanceEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val balanceKafkaProperties: BalanceKafkaProperties,
    private val objectMapper: ObjectMapper
) : IBalanceEventPublisher {

    override fun publishCharge(balanceId: Long, amount: Long, balance: Long) {
        publish(balanceId, amount, balance, TransactionType.CHARGE)
    }

    override fun publishUse(balanceId: Long, amount: Long, balance: Long) {
        publish(balanceId, amount, balance, TransactionType.USE)
    }

    private fun publish(balanceId: Long, amount: Long, balance: Long, type: TransactionType) {
        val document = BalanceHistoryDocument(
            balanceId = balanceId,
            amount = amount,
            balance = balance,
            transactionType = type.name
        )
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = balanceKafkaProperties.groupId,
            topic = balanceKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(document)
        )
        applicationEventPublisher.publishEvent(outboxEvent)
    }

    private enum class TransactionType { CHARGE, USE }
}
