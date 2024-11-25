package com.hhplus.ecommerce.infrastructure.outboxevent.jpa

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OutboxEventJpaRepository: JpaRepository<OutboxEventEntity, UUID> {
    fun findAllByTopicAndStatus(topic: String, status: OutboxEventStatus): List<OutboxEventEntity>

    @Query("SELECT e FROM OutboxEventEntity e WHERE e.topic = :topic AND e.status = :status AND e.retryCnt <= :maxCnt")
    fun findAllByTopicStatusAndMaxRetryCnt(
        @Param("topic") topic: String,
        @Param("status") status: OutboxEventStatus,
        @Param("maxCnt") maxCnt: Int
    ): List<OutboxEventEntity>

}