package com.hhplus.ecommerce.payment.usecase.dto

import com.hhplus.ecommerce.payment.common.PayMethod

/**
 * 결제 수단별 금액 분해 DTO
 *
 * @param balanceAmount  서비스 잔액으로 결제할 금액
 * @param pointAmount    포인트로 결제할 금액 (추후 포인트 도메인 연동)
 * @param cardAmount     카드로 결제할 금액 (외부 PG 연동)
 * @param couponDiscount 쿠폰 할인 금액
 *
 * totalAmount = balanceAmount + pointAmount + cardAmount
 * actualPayAmount = totalAmount - couponDiscount (주문 금액과 일치해야 함)
 */
data class PaymentBreakdown(
    val balanceAmount: Long = 0L,
    val pointAmount: Long = 0L,
    val cardAmount: Long = 0L,
    val couponDiscount: Long = 0L
) {
    /** 실제 결제 총액 */
    val totalAmount: Long get() = balanceAmount + pointAmount + cardAmount

    /** 결제 수단 자동 판별 */
    val payMethod: PayMethod get() = when {
        balanceAmount > 0 && cardAmount == 0L && pointAmount == 0L -> PayMethod.BALANCE
        pointAmount > 0 && balanceAmount == 0L && cardAmount == 0L -> PayMethod.POINT
        cardAmount > 0 && balanceAmount == 0L && pointAmount == 0L -> PayMethod.CREDIT_CARD
        else -> PayMethod.MIXED
    }

    companion object {
        /** 잔액 단독 결제 */
        fun balanceOnly(amount: Long, couponDiscount: Long = 0L) =
            PaymentBreakdown(balanceAmount = amount, couponDiscount = couponDiscount)

        /** 카드 단독 결제 */
        fun cardOnly(amount: Long, couponDiscount: Long = 0L) =
            PaymentBreakdown(cardAmount = amount, couponDiscount = couponDiscount)

        /** 잔액 + 카드 복합 */
        fun balanceAndCard(balanceAmount: Long, cardAmount: Long, couponDiscount: Long = 0L) =
            PaymentBreakdown(balanceAmount = balanceAmount, cardAmount = cardAmount, couponDiscount = couponDiscount)
    }
}
