package com.hhplus.ecommerce.notification.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.notification.domain.NotificationService
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationKafkaConsumer(
    private val notificationService: NotificationService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(NotificationKafkaConsumer::class.java)

    companion object {
        private val SUPPORTED_VERSIONS = setOf("1")
    }

    @KafkaListener(
        groupId = "\${hhplus.kafka.notification.group-id}",
        topics = ["\${hhplus.kafka.notification.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        if (event.schemaVersion !in SUPPORTED_VERSIONS) {
            logger.warn("NOTIFICATION:KAFKA:CONSUMER:UNSUPPORTED_VERSION - version=${event.schemaVersion}, eventId=${event.id}")
            return
        }
        try {
            val notification = objectMapper.readValue(event.payload, NotificationEvent::class.java)
            logger.info("NOTIFICATION:KAFKA:CONSUMER - type=${notification.type}, userId=${notification.userId}")
            notificationService.send(notification)
        } catch (e: Exception) {
            logger.error("NOTIFICATION:KAFKA:CONSUMER:ERROR - eventId=${event.id}", e)
        }
    }
}
