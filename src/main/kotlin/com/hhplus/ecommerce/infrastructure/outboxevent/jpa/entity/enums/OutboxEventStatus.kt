package com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums

enum class OutboxEventStatus {
    INIT, PUBLISH, SUCCESS, FAILED
}