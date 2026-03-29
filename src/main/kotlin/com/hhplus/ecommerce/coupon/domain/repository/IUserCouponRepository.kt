package com.hhplus.ecommerce.coupon.domain.repository

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity

interface IUserCouponRepository {
    fun findByUserId(userId: Long): List<UserCouponEntity>
    fun findByUserIdAndCouponPolicyId(userId: Long, couponPolicyId: Long): UserCouponEntity?
    fun findById(id: Long): UserCouponEntity
    fun findIssuedByPolicyIds(couponPolicyIds: List<Long>): List<UserCouponEntity>
    fun save(entity: UserCouponEntity): UserCouponEntity
    fun saveAll(entities: List<UserCouponEntity>): List<UserCouponEntity>
}
