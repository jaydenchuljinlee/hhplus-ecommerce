package com.hhplus.ecommerce.balance.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.balance.domain.event.BalanceChangedEvent
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryDocument
import com.hhplus.ecommerce.common.properties.BalanceKafkaProperties
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BalanceOutboxEventHandler(
    private val balanceKafkaProperties: BalanceKafkaProperties,
    private val objectMapper: ObjectMapper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @EventListener
    fun handle(event: BalanceChangedEvent) {
        val document = BalanceHistoryDocument(
            balanceId = event.balanceId,
            amount = event.amount,
            balance = event.balance,
            transactionType = event.transactionType
        )

        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = balanceKafkaProperties.groupId,
            topic = balanceKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(document),
            eventType = "Balance",
            schemaVersion = "1"
        )

        applicationEventPublisher.publishEvent(outboxEvent)
    }
}
