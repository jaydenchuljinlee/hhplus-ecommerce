package com.hhplus.ecommerce.payment.api.dto

import com.hhplus.ecommerce.payment.usecase.dto.PaymentBreakdown
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
    @Schema(description = "결제 수단별 금액 분해 (null이면 잔액 단독 결제 — 하위 호환)")
    var breakdown: PaymentBreakdownRequest? = null
) {

    fun toPaymentCreation(): PaymentCreation {
        return PaymentCreation(
            orderId = orderId,
            userId = userId,
            breakdown = breakdown?.toBreakdown()
        )
    }
}

/**
 * API 레이어 결제 수단별 금액 분해 요청 DTO
 */
data class PaymentBreakdownRequest(
    @Schema(description = "잔액 결제 금액", example = "5000")
    val balanceAmount: Long = 0L,
    @Schema(description = "포인트 결제 금액 (추후 포인트 도메인 연동)", example = "0")
    val pointAmount: Long = 0L,
    @Schema(description = "카드 결제 금액 (외부 PG 연동)", example = "5000")
    val cardAmount: Long = 0L,
    @Schema(description = "쿠폰 할인 금액", example = "0")
    val couponDiscount: Long = 0L
) {
    fun toBreakdown(): PaymentBreakdown = PaymentBreakdown(
        balanceAmount = balanceAmount,
        pointAmount = pointAmount,
        cardAmount = cardAmount,
        couponDiscount = couponDiscount
    )
}