package com.hhplus.ecommerce.outboxevent.domain

import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import java.util.UUID

interface IOutboxEventRepository {
    fun findById(id: UUID): OutboxEventEntity
    fun findAllByTopicStatusAndMaxRetryCnt(topic: String, status: OutboxEventStatus): List<OutboxEventEntity>

    /**
     * 재시도 횟수가 [MAX_CNT]를 초과한 이벤트 조회 — 수동 처리 대상 식별용
     */
    fun findExhaustedEvents(topic: String, status: OutboxEventStatus): List<OutboxEventEntity>

    fun insertOrUpdate(event: OutboxEventEntity): OutboxEventEntity
}