package com.hhplus.ecommerce.infrastructure.outboxevent.jpa

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OutboxEventJpaRepository: JpaRepository<OutboxEventEntity, UUID> {
}