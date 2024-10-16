package com.hhplus.ecommerce.domain.payment.dto

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity

data class PaymentResult(
    var orderId: Long,
    var status: String,
    var price: Long,
) {
    companion object {
        fun from(entity: PaymentEntity): PaymentResult {
            return PaymentResult(
                orderId = entity.orderId,
                status = entity.status,
                price = entity.price,
            )
        }
    }
}