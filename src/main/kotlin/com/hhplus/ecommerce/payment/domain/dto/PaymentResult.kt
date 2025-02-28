package com.hhplus.ecommerce.payment.domain.dto

import com.hhplus.ecommerce.payment.common.PayStatus


data class PaymentResult(
    var paymentId: Long,
    var userId: Long,
    var orderId: Long,
    var status: PayStatus,
    var price: Long,
)