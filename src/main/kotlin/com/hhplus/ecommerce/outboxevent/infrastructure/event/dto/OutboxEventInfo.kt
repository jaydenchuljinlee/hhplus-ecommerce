package com.hhplus.ecommerce.outboxevent.infrastructure.event.dto

import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import java.util.UUID

data class OutboxEventInfo(
    var id: UUID,
    val groupId: String,
    val topic: String,
    val payload: String,
    val eventType: String = "",
    val schemaVersion: String = "1",
    val partitionKey: String? = null  // Q63: 동일 주문 이벤트를 같은 파티션으로 라우팅
) {
    fun toEntity(): OutboxEventEntity {
        return OutboxEventEntity(
            id = id,
            groupId = groupId,
            topic = topic,
            payload = payload,
            eventType = eventType,
            schemaVersion = schemaVersion,
            status = OutboxEventStatus.INIT
        )
    }
}