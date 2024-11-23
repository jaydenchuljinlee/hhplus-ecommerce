package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo

interface PaymentEventListener {
    fun listen(event: OutboxEventInfo)
}