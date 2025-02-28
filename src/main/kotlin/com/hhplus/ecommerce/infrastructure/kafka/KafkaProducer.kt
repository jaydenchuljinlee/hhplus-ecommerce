package com.hhplus.ecommerce.infrastructure.kafka

import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, OutboxEventInfo>
) {
    fun sendOutboxEvent(event: OutboxEventInfo) {
        kafkaTemplate.send(event.topic, event)
    }
}