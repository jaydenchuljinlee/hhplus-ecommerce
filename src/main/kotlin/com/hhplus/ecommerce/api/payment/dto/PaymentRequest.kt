package com.hhplus.ecommerce.api.payment.dto

import com.hhplus.ecommerce.usercase.payment.dto.PaymentCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

class PaymentCreationRequest(
    @Parameter(description = "주문 ID", required = true)
    @Schema(description = "주문 ID", example = "1")
    var orderId: Long,
    @Schema(description = "사용자 ID", example = "1")
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
) {

    fun toPaymentCreation(): PaymentCreation {
        return PaymentCreation(
            orderId = orderId,
            userId = userId
        )
    }
}