package com.hhplus.ecommerce.infrastructure.outboxevent

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.OutboxEventJpaRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OutboxEventRepository(
    private val outboxEventJpaRepository: OutboxEventJpaRepository
) {
    fun findById(id: UUID): OutboxEventEntity {
        return outboxEventJpaRepository.findById(id).orElseThrow { IllegalArgumentException("존재하지 않는 이벤트입니다 => $id") }
    }

    fun findAllByTopicAndStatus(topic: String, status: OutboxEventStatus): List<OutboxEventEntity> {
        return outboxEventJpaRepository.findAllByTopicAndStatus(topic, status)
    }

    fun insertOrUpdate(event: OutboxEventEntity): OutboxEventEntity {
        return outboxEventJpaRepository.saveAndFlush(event)
    }
}