package com.hhplus.ecommerce.usercase.payment.dto

import com.hhplus.ecommerce.domain.payment.dto.PaymentResult

data class PaymentInfo(
    var orderId: Long,
    var status: String,
    var price: Long,
) {
    companion object {
        fun from(result: PaymentResult): PaymentInfo {
            return PaymentInfo(
                orderId = result.orderId,
                status = result.status,
                price = result.price,
            )
        }
    }
}