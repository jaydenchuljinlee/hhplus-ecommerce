package com.hhplus.ecommerce.payment.usecase.dto

data class PaymentBreakdown(
    val balanceAmount: Long = 0,
    val pointAmount: Long = 0,
    val cardAmount: Long = 0,
    val couponDiscount: Long = 0
) {
    init {
        require(balanceAmount >= 0) { "balanceAmount는 0 이상이어야 합니다." }
        require(pointAmount >= 0) { "pointAmount는 0 이상이어야 합니다." }
        require(cardAmount >= 0) { "cardAmount는 0 이상이어야 합니다." }
        require(couponDiscount >= 0) { "couponDiscount는 0 이상이어야 합니다." }
    }

    val totalAmount: Long get() = balanceAmount + pointAmount + cardAmount
}
