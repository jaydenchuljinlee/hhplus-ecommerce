package com.hhplus.ecommerce.api.payment.dto

import io.swagger.v3.oas.annotations.Parameter

class PaymentCreationRequest(
    @Parameter(description = "주문 ID", required = true)
    var orderId: Long,
) {
    companion object {
        fun getInstance() = PaymentCreationRequest(
            orderId = 0L,
        )
    }
}