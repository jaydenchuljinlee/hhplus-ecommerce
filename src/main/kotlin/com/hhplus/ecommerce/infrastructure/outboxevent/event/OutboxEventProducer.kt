package com.hhplus.ecommerce.infrastructure.outboxevent.event

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity

interface OutboxEventProducer {
    fun beforeCommit(event: OutboxEventEntity)
    fun afterCommit(event: OutboxEventEntity)
}