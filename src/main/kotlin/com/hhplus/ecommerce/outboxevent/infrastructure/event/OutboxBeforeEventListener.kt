package com.hhplus.ecommerce.outboxevent.infrastructure.event

import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
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