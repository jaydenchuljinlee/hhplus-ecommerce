package com.hhplus.ecommerce.coupon.infrastructure

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.domain.repository.IUserCouponRepository
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponNotFoundException
import com.hhplus.ecommerce.coupon.infrastructure.jpa.UserCouponJpaRepository
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import org.springframework.stereotype.Component

@Component
class UserCouponRepository(
    private val jpaRepository: UserCouponJpaRepository
) : IUserCouponRepository {

    override fun findByUserId(userId: Long): List<UserCouponEntity> =
        jpaRepository.findByUserId(userId)

    override fun findByUserIdAndCouponPolicyId(userId: Long, couponPolicyId: Long): UserCouponEntity? =
        jpaRepository.findByUserIdAndCouponPolicyId(userId, couponPolicyId)

    override fun findById(id: Long): UserCouponEntity =
        jpaRepository.findById(id).orElseThrow { CouponNotFoundException("사용자 쿠폰을 찾을 수 없습니다. id=$id") }

    override fun findIssuedByPolicyIds(couponPolicyIds: List<Long>): List<UserCouponEntity> =
        jpaRepository.findByStatusAndCouponPolicyIdIn(UserCouponStatus.ISSUED, couponPolicyIds)

    override fun save(entity: UserCouponEntity): UserCouponEntity =
        jpaRepository.save(entity)

    override fun saveAll(entities: List<UserCouponEntity>): List<UserCouponEntity> =
        jpaRepository.saveAll(entities)
}
