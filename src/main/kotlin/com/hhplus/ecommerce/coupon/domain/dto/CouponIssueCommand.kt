package com.hhplus.ecommerce.coupon.domain.dto

data class CouponIssueCommand(
    val userId: Long,
    val couponPolicyId: Long
)
