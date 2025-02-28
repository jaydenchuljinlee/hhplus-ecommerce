package com.hhplus.ecommerce.outboxevent.domain

import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import java.util.UUID

interface IOutboxEventRepository {
    fun findById(id: UUID): OutboxEventEntity
    fun findAllByTopicStatusAndMaxRetryCnt(topic: String, status: OutboxEventStatus): List<OutboxEventEntity>
    fun insertOrUpdate(event: OutboxEventEntity): OutboxEventEntity
}