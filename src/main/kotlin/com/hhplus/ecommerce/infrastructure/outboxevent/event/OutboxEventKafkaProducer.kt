package com.hhplus.ecommerce.infrastructure.outboxevent.event

import com.hhplus.ecommerce.infrastructure.outboxevent.OutboxEventRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OutboxEventKafkaProducer(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
): OutboxEventProducer {
    private val logger = LoggerFactory.getLogger(OutboxEventKafkaProducer::class.java)

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    override fun beforeCommit(event: OutboxEventEntity) {
        try {
            logger.info("OUTBOX_EVENT:PUBLISHER:BEFORE_COMMIT $event")

            outboxEventRepository.insertOrUpdate(event)
        } catch (e: Exception) {
            logger.error("OUTBOX_EVENT:PUBLISHER:BEFORE_COMMIT:ERROR => event_id ${event.id}", e)
            // 특정 행동 유발
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    override fun afterCommit(event: OutboxEventEntity) {
        val entity = outboxEventRepository.findById(event.id)

        try {
            logger.info("OUTBOX_EVENT:PUBLISHER:AFTER_COMMIT $event")
            kafkaTemplate.send(event.topic, event.payload)
            entity.updateStatus(OutboxEventStatus.PUBLISH)
        } catch (e: Exception) {
            logger.error("OUTBOX_EVENT:PUBLISHER:AFTER_COMMIT:FAILED => event_id ${event.id}", e)
            entity.updateStatus(OutboxEventStatus.FAILED)
        } finally {
            outboxEventRepository.insertOrUpdate(entity)
        }
    }
}