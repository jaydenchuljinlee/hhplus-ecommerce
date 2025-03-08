package com.hhplus.ecommerce.outboxevent.infrastructure.event

import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OutboxEventListener(
    private val outboxEventService: OutboxEventService,
    private val kafkaProducer: KafkaProducer
) {
    private val logger = LoggerFactory.getLogger(OutboxEventListener::class.java)

    @EventListener
    fun handleOutboxEvent(event: OutboxEventInfo) {
        logger.info("OUTBOX_EVENT:LISTENER:BEFORE_COMMIT: $event")
        outboxEventService.insertOrUpdate(event.toEntity())
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publish(event: OutboxEventInfo) {
        try {
            logger.info("OUTBOX_EVENT:LISTENER:AFTER_COMMIT $event")
            outboxEventService.updateStatus(event.id, OutboxEventStatus.PUBLISH)
            kafkaProducer.sendOutboxEvent(event)
        } catch (e: Exception) {
            logger.error("OUTBOX_EVENT:LISTENER:AFTER_COMMIT:FAILED => event_id ${event.id}", e)
            outboxEventService.updateStatus(event.id, OutboxEventStatus.FAILED)
        }
    }
}