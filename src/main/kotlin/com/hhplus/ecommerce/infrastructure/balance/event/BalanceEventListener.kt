package com.hhplus.ecommerce.infrastructure.balance.event

import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo

interface BalanceEventListener {
    fun listener(event: OutboxEventInfo)
}