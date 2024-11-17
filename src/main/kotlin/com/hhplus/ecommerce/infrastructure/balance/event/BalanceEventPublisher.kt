package com.hhplus.ecommerce.infrastructure.balance.event

import com.hhplus.ecommerce.infrastructure.balance.mongodb.BalanceHistoryDocument
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class BalanceEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) {
    fun publish(event: BalanceHistoryDocument) {
        eventPublisher.publishEvent(event)
    }
}