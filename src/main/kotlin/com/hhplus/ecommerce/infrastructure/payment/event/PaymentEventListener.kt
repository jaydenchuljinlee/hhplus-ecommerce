package com.hhplus.ecommerce.infrastructure.payment.event

interface PaymentEventListener {
    fun listen(message: String)
}