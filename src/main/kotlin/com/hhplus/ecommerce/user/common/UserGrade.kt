package com.hhplus.ecommerce.user.common

enum class UserGrade(val minAmount: Long) {
    NORMAL(0L),
    SILVER(100_000L),
    GOLD(500_000L),
    VIP(1_000_000L),
    VVIP(5_000_000L);

    companion object {
        fun from(totalPurchaseAmount: Long): UserGrade =
            entries.sortedByDescending { it.minAmount }
                .first { totalPurchaseAmount >= it.minAmount }
    }
}
