package com.hhplus.ecommerce.outboxevent.infrastructure.event.dto

import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
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