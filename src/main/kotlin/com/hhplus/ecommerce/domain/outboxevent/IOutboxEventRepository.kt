package com.hhplus.ecommerce.domain.outboxevent

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import java.util.UUID

interface IOutboxEventRepository {
    fun findById(id: UUID): OutboxEventEntity
    fun findAllByTopicStatusAndMaxRetryCnt(topic: String, status: OutboxEventStatus): List<OutboxEventEntity>
    fun insertOrUpdate(event: OutboxEventEntity): OutboxEventEntity
}