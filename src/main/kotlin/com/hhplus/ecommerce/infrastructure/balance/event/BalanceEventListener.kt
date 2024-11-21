package com.hhplus.ecommerce.infrastructure.balance.event

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity

interface BalanceEventListener {
    fun listener(event: OutboxEventEntity)
}