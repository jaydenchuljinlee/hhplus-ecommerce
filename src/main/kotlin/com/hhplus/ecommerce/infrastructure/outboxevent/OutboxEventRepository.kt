package com.hhplus.ecommerce.infrastructure.outboxevent

import com.hhplus.ecommerce.domain.outboxevent.IOutboxEventRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.OutboxEventJpaRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
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