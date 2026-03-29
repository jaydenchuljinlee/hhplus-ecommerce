package com.hhplus.ecommerce.coupon.domain.repository

import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity

import java.time.LocalDateTime

interface ICouponPolicyRepository {
    fun findById(id: Long): CouponPolicyEntity
    fun findByIdWithLock(id: Long): CouponPolicyEntity
    fun findExpiredPolicies(now: LocalDateTime): List<CouponPolicyEntity>
    fun save(entity: CouponPolicyEntity): CouponPolicyEntity
}
