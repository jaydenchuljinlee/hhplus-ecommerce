package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity

interface PaymentEventListener {
    fun listen(event: OutboxEventEntity)
}