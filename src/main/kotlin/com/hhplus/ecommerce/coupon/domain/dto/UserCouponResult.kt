package com.hhplus.ecommerce.coupon.domain.dto

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import java.time.LocalDateTime

data class UserCouponResult(
    val id: Long,
    val userId: Long,
    val couponPolicyId: Long,
    val status: UserCouponStatus,
    val issuedAt: LocalDateTime,
    val usedAt: LocalDateTime?,
    val orderId: Long?
) {
    companion object {
        fun from(entity: UserCouponEntity) = UserCouponResult(
            id = entity.id,
            userId = entity.userId,
            couponPolicyId = entity.couponPolicyId,
            status = entity.status,
            issuedAt = entity.issuedAt,
            usedAt = entity.usedAt,
            orderId = entity.orderId
        )
    }
}
