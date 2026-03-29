package com.hhplus.ecommerce.user.common

/**
 * 사용자 등급
 *
 * 누적 구매 금액 기준으로 자동 산정된다.
 *
 * NORMAL  :           0원 이상
 * SILVER  :     100,000원 이상
 * GOLD    :     500,000원 이상
 * VIP     :   1,000,000원 이상
 * VVIP    :   5,000,000원 이상
 */
enum class UserGrade(val minPurchaseAmount: Long) {
    NORMAL(0L),
    SILVER(100_000L),
    GOLD(500_000L),
    VIP(1_000_000L),
    VVIP(5_000_000L);

    companion object {
        /** 누적 구매 금액에 맞는 등급 산정 */
        fun of(totalPurchaseAmount: Long): UserGrade {
            return entries
                .sortedByDescending { it.minPurchaseAmount }
                .first { totalPurchaseAmount >= it.minPurchaseAmount }
        }
    }
}
