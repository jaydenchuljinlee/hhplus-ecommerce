package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class PaymentSpringEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
): PaymentEventPublisher {
    override fun publish(paymentHistoryDocument: PaymentHistoryDocument) {
        eventPublisher.publishEvent(paymentHistoryDocument)
    }
}