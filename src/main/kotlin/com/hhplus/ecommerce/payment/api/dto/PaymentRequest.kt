package com.hhplus.ecommerce.payment.api.dto

import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

class PaymentCreationRequest(
    @field:Min(1, message = "orderId는 1 이상이어야 합니다.")
    @Parameter(description = "주문 ID", required = true)
    @Schema(description = "주문 ID", example = "1")
    var orderId: Long,
    @field:Min(1, message = "userId는 1 이상이어야 합니다.")
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