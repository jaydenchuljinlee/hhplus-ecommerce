package com.hhplus.ecommerce.infrastructure.outboxevent.event

import com.hhplus.ecommerce.domain.outboxevent.OutboxEventService
import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class OutboxBeforeEventListener(
    private val outboxEventService: OutboxEventService
) {
    private val logger = LoggerFactory.getLogger(OutboxBeforeEventListener::class.java)

    @EventListener
    fun handleOutboxEvent(event: OutboxEventInfo) {
        logger.info("OUTBOX_EVENT:LISTENER:BEFORE_COMMIT: $event")
        outboxEventService.insertOrUpdate(event.toEntity())
    }
}