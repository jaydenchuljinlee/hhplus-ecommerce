package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument

interface PaymentEventListener {
    fun listen(event: PaymentHistoryDocument)
}