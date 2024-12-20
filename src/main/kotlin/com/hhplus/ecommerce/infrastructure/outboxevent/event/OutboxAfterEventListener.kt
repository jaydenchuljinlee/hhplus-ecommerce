package com.hhplus.ecommerce.infrastructure.outboxevent.event

import com.hhplus.ecommerce.domain.outboxevent.OutboxEventService
import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OutboxAfterEventListener(
    private val outboxEventService: OutboxEventService,
    private val kafkaProducer: KafkaProducer
) {
    private val logger = LoggerFactory.getLogger(OutboxAfterEventListener::class.java)

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