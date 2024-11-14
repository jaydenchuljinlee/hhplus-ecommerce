package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class PaymentEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) {
    fun publish(paymentHistoryDocument: PaymentHistoryDocument) {
        eventPublisher.publishEvent(paymentHistoryDocument)
    }
}