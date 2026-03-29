package com.hhplus.ecommerce.coupon.domain.dto

import com.hhplus.ecommerce.coupon.common.CouponType
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity
import java.time.LocalDateTime

data class CouponPolicyResult(
    val id: Long,
    val name: String,
    val type: CouponType,
    val discountValue: Long,
    val minOrderAmount: Long,
    val maxDiscountAmount: Long?,
    val totalQuantity: Int,
    val issuedQuantity: Int,
    val startAt: LocalDateTime,
    val expiredAt: LocalDateTime
) {
    companion object {
        fun from(entity: CouponPolicyEntity) = CouponPolicyResult(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            discountValue = entity.discountValue,
            minOrderAmount = entity.minOrderAmount,
            maxDiscountAmount = entity.maxDiscountAmount,
            totalQuantity = entity.totalQuantity,
            issuedQuantity = entity.issuedQuantity,
            startAt = entity.startAt,
            expiredAt = entity.expiredAt
        )
    }
}
