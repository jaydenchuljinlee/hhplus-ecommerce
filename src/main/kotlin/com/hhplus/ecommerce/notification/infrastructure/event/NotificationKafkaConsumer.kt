package com.hhplus.ecommerce.notification.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.notification.domain.NotificationService
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationKafkaConsumer(
    private val notificationService: NotificationService,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(NotificationKafkaConsumer::class.java)

    @KafkaListener(
        groupId = "\${hhplus.kafka.notification.group-id}",
        topics = ["\${hhplus.kafka.notification.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        // 멱등성 보장: 이미 성공 처리된 이벤트 스킵
        val outboxEvent = outboxEventRepository.findById(event.id)
        if (outboxEvent.status == OutboxEventStatus.SUCCESS) {
            logger.info("NOTIFICATION:KAFKA:CONSUMER:SKIP 이미 처리된 이벤트: eventId={}", event.id)
            return
        }

        try {
            val notificationEvent = objectMapper.readValue(event.payload, NotificationEvent::class.java)
            logger.info(
                "NOTIFICATION:KAFKA:CONSUMER userId={}, type={}, orderId={}",
                notificationEvent.userId, notificationEvent.type, notificationEvent.orderId
            )

            notificationService.send(notificationEvent)

            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)

        } catch (e: Exception) {
            logger.error("NOTIFICATION:KAFKA:CONSUMER:ERROR eventId={}", event.id, e)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }
}
