package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument

interface PaymentEventPublisher {
    fun publish(paymentHistoryDocument: PaymentHistoryDocument)
}