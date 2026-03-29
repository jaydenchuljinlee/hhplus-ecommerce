package com.hhplus.ecommerce.payment.usecase.dto

/**
 * 결제 요청 Usecase DTO
 *
 * @param breakdown null이면 기존 잔액 단독 결제 동작 유지 (하위 호환)
 */
data class PaymentCreation(
    var orderId: Long,
    var userId: Long,
    var breakdown: PaymentBreakdown? = null
)