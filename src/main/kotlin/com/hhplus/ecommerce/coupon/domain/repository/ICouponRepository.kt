package com.hhplus.ecommerce.coupon.domain.repository

import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.CouponPolicyEntity

interface ICouponRepository {
    fun findById(couponPolicyId: Long): CouponPolicyEntity
    fun findByIdWithLock(couponPolicyId: Long): CouponPolicyEntity
    fun save(entity: CouponPolicyEntity): CouponPolicyEntity
}
