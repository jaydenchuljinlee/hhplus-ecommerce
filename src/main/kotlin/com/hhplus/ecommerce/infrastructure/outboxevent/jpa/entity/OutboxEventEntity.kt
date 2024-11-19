package com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.UUID

@Entity @Table(name = "outbox_event")
class OutboxEventEntity(
    @Id
    var id: UUID,
    @Column(name = "group_id")
    var groupId: String,
    @Column(name = "topic")
    var topic: String,
    @Column(name = "payload")
    var payload: String,
    @Column(name = "status") @Enumerated(EnumType.STRING)
    var status: OutboxEventStatus = OutboxEventStatus.INIT,
    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun updateStatus(status: OutboxEventStatus) {
        this.status = status
    }
}