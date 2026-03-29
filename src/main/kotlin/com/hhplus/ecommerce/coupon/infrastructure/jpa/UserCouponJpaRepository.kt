package com.hhplus.ecommerce.coupon.infrastructure.jpa

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCouponJpaRepository : JpaRepository<UserCouponEntity, Long> {
    fun findByUserId(userId: Long): List<UserCouponEntity>
    fun findByUserIdAndCouponPolicyId(userId: Long, couponPolicyId: Long): UserCouponEntity?
    fun findByStatusAndCouponPolicyIdIn(status: UserCouponStatus, couponPolicyIds: List<Long>): List<UserCouponEntity>
}
