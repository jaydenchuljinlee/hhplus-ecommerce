package com.hhplus.ecommerce.infrastructure.outboxevent.jpa

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OutboxEventJpaRepository: JpaRepository<OutboxEventEntity, UUID> {
    fun findAllByTopicAndStatus(topic: String, status: OutboxEventStatus): List<OutboxEventEntity>
}