package com.hhplus.ecommerce.payment.api.dto

import com.hhplus.ecommerce.payment.usecase.dto.PaymentBreakdown
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

class PaymentCreationRequest(
    @Parameter(description = "주문 ID", required = true)
    @Schema(description = "주문 ID", example = "1")
    var orderId: Long,
    @Schema(description = "사용자 ID", example = "1")
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
    @Schema(description = "복합 결제 내역 (미입력 시 전액 잔액 차감)")
    var breakdown: PaymentBreakdown? = null,
) {

    fun toPaymentCreation(): PaymentCreation {
        return PaymentCreation(
            orderId = orderId,
            userId = userId,
            breakdown = breakdown
        )
    }
}