package com.hhplus.ecommerce.notification.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.NotificationKafkaProperties
import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.notification.domain.INotificationEventPublisher
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NotificationEventPublisher(
    private val kafkaProducer: KafkaProducer,
    private val notificationKafkaProperties: NotificationKafkaProperties,
    private val objectMapper: ObjectMapper
) : INotificationEventPublisher {

    private val logger = LoggerFactory.getLogger(NotificationEventPublisher::class.java)

    override fun publish(event: NotificationEvent) {
        try {
            val outboxEvent = OutboxEventInfo(
                id = UUID.randomUUID(),
                groupId = notificationKafkaProperties.groupId,
                topic = notificationKafkaProperties.topic,
                payload = objectMapper.writeValueAsString(event),
                eventType = "Notification",
                schemaVersion = "1"
            )
            kafkaProducer.sendOutboxEvent(outboxEvent)
        } catch (e: Exception) {
            logger.warn("NOTIFICATION:PUBLISH:FAILED - type=${event.type}, userId=${event.userId}", e)
        }
    }
}
