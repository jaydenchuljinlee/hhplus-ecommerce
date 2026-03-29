package com.hhplus.ecommerce.coupon.api.dto

import com.hhplus.ecommerce.coupon.domain.dto.CouponIssueCommand
import jakarta.validation.constraints.Min

data class CouponIssueRequest(
    @field:Min(1) val userId: Long,
    @field:Min(1) val couponPolicyId: Long
) {
    fun toCommand() = CouponIssueCommand(userId = userId, couponPolicyId = couponPolicyId)
}
