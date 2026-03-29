package com.hhplus.ecommerce.coupon.domain.repository

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import java.time.LocalDateTime

interface IUserCouponRepository {
    fun save(entity: UserCouponEntity): UserCouponEntity
    fun findById(userCouponId: Long): UserCouponEntity
    fun findByUserId(userId: Long): List<UserCouponEntity>
    fun findByUserIdAndStatus(userId: Long, status: UserCouponStatus): List<UserCouponEntity>
    fun existsByUserIdAndCouponPolicyId(userId: Long, couponPolicyId: Long): Boolean
    fun findExpiredIssuedCoupons(now: LocalDateTime): List<UserCouponEntity>
    fun saveAll(entities: List<UserCouponEntity>): List<UserCouponEntity>
}
