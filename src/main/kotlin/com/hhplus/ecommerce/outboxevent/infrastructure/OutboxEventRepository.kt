package com.hhplus.ecommerce.outboxevent.infrastructure

import com.hhplus.ecommerce.outboxevent.domain.IOutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.OutboxEventJpaRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OutboxEventRepository(
    private val outboxEventJpaRepository: OutboxEventJpaRepository
): IOutboxEventRepository {
    private val MAX_CNT = 3

    override fun findById(id: UUID): OutboxEventEntity {
        return outboxEventJpaRepository.findById(id).orElseThrow { IllegalArgumentException("존재하지 않는 이벤트입니다 => $id") }
    }

    override fun findAllByTopicStatusAndMaxRetryCnt(topic: String, status: OutboxEventStatus): List<OutboxEventEntity> {
        return outboxEventJpaRepository.findAllByTopicStatusAndMaxRetryCnt(topic, status, MAX_CNT)
    }

    override fun insertOrUpdate(event: OutboxEventEntity): OutboxEventEntity {
        return outboxEventJpaRepository.save(event)
    }
}