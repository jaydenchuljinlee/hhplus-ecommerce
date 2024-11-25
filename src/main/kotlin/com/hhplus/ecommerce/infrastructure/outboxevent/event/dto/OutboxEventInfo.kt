package com.hhplus.ecommerce.infrastructure.outboxevent.event.dto

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import java.util.UUID

data class OutboxEventInfo(
    var id: UUID,
    val groupId: String,
    val topic: String,
    val payload: String
) {
    fun toEntity(): OutboxEventEntity {
        return OutboxEventEntity(
            id = id,
            groupId = groupId,
            topic = topic,
            payload = payload,
            status = OutboxEventStatus.INIT
        )
    }


}