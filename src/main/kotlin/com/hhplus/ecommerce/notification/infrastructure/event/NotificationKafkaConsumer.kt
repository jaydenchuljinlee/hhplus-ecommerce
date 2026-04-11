package com.hhplus.ecommerce.notification.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.notification.domain.NotificationService
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
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
    fun listen(event: OutboxEventInfo, ack: Acknowledgment) {  // Q68: manual commit
        if (event.schemaVersion !in SUPPORTED_VERSIONS) {
            logger.warn("NOTIFICATION:KAFKA:CONSUMER:UNSUPPORTED_VERSION - version=${event.schemaVersion}, eventId=${event.id}")
            ack.acknowledge()
            return
        }
        // NOTE: Notification 이벤트는 Outbox DB에 저장되지 않으므로 DB 기반 멱등성 체크 미적용
        try {
            val notification = objectMapper.readValue(event.payload, NotificationEvent::class.java)
            logger.info("NOTIFICATION:KAFKA:CONSUMER - type=${notification.type}, userId=${notification.userId}")
            notificationService.send(notification)
            ack.acknowledge()  // Q68: 처리 완료 후 offset commit
        } catch (e: Exception) {
            logger.error("NOTIFICATION:KAFKA:CONSUMER:ERROR - eventId=${event.id}", e)
            // ack 미호출 → DLQ ErrorHandler가 재시도 후 DLT로 이동 (Q65)
        }
    }
}
