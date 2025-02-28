package com.hhplus.ecommerce.payment.api.dto

import com.hhplus.ecommerce.payment.usecase.dto.PaymentInfo
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

class PaymentResponse(
    @Parameter(description = "결제 ID", required = true)
    @Schema(description = "결제 ID", example = "1")
    var id: Long,
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Parameter(description = "주문 ID", required = true)
    @Schema(description = "주문 ID", example = "1")
    var orderId: Long,
    @Parameter(description = "결제 가격", required = true)
    @Schema(description = "결제 가격", example = "1000")
    var price: Long,
    @Parameter(description = "결제 상태", required = true)
    @Schema(description = "결제 상태", example = "PAYMENT_COMPLETED")
    var status: String,
) {
    companion object {
        fun from(info: PaymentInfo): PaymentResponse {
            return PaymentResponse(
                id = info.orderId,
                userId = info.orderId,
                orderId = info.orderId,
                price = info.price,
                status = "PAYMENT_COMPLETED"
            )
        }
    }

}