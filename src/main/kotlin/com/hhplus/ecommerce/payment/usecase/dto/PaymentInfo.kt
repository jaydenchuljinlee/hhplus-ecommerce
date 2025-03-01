package com.hhplus.ecommerce.payment.usecase.dto

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.payment.common.PayStatus
import com.hhplus.ecommerce.payment.domain.dto.PaymentResult

data class PaymentInfo(
    var paymentId: Long,
    var userId: Long,
    var orderId: Long,
    var status: PayStatus,
    var price: Long,
) {
    companion object {
        fun from(result: PaymentResult): PaymentInfo {
            return PaymentInfo(
                paymentId = result.paymentId,
                userId = result.userId,
                orderId = result.orderId,
                status = result.status,
                price = result.price,
            )
        }
    }
}