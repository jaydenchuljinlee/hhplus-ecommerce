package com.hhplus.ecommerce.coupon.api.dto

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.domain.dto.UserCouponResult
import java.time.LocalDateTime

data class UserCouponResponse(
    val id: Long,
    val userId: Long,
    val couponPolicyId: Long,
    val status: UserCouponStatus,
    val issuedAt: LocalDateTime,
    val usedAt: LocalDateTime?,
    val orderId: Long?
) {
    companion object {
        fun from(result: UserCouponResult) = UserCouponResponse(
            id = result.id,
            userId = result.userId,
            couponPolicyId = result.couponPolicyId,
            status = result.status,
            issuedAt = result.issuedAt,
            usedAt = result.usedAt,
            orderId = result.orderId
        )
    }
}
