package com.hhplus.ecommerce.notification.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.NotificationKafkaProperties
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.notification.domain.event.INotificationEventPublisher
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 알림 이벤트 발행 구현체 (Outbox 패턴)
 *
 * applicationEventPublisher → OutboxEventListener(BEFORE_COMMIT 저장 + AFTER_COMMIT Kafka 발행)
 */
@Component
class NotificationEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val notificationKafkaProperties: NotificationKafkaProperties,
    private val objectMapper: ObjectMapper
) : INotificationEventPublisher {

    override fun publish(event: NotificationEvent) {
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = notificationKafkaProperties.groupId,
            topic = notificationKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(event)
        )
        applicationEventPublisher.publishEvent(outboxEvent)
    }
}
