package com.hhplus.ecommerce.outboxevent.domain.dto

import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import java.time.LocalDateTime
import java.util.UUID

data class OutboxResult(
    var id: UUID,
    var groupId: String,
    var topic: String,
    var payload: String,
    var status: OutboxEventStatus,
    var retryCnt: Int,
    var createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: OutboxEventEntity): OutboxResult {
            return OutboxResult(
                id = entity.id,
                groupId = entity.groupId,
                topic = entity.topic,
                payload = entity.payload,
                status = entity.status,
                retryCnt = entity.retryCnt,
                createdAt = entity.createdAt
            )
        }
    }

    fun toOutboxEventInfo(): OutboxEventInfo {
        return OutboxEventInfo(
            id = id,
            groupId = groupId,
            topic = topic,
            payload = payload
        )
    }
}