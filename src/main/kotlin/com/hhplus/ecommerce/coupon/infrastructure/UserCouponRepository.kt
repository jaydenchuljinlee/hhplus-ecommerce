package com.hhplus.ecommerce.coupon.infrastructure

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.domain.repository.IUserCouponRepository
import com.hhplus.ecommerce.coupon.infrastructure.exception.UserCouponNotFoundException
import com.hhplus.ecommerce.coupon.infrastructure.jpa.UserCouponJpaRepository
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserCouponRepository(
    private val userCouponJpaRepository: UserCouponJpaRepository
) : IUserCouponRepository {

    override fun save(entity: UserCouponEntity): UserCouponEntity {
        return userCouponJpaRepository.save(entity)
    }

    override fun findById(userCouponId: Long): UserCouponEntity {
        return userCouponJpaRepository.findById(userCouponId)
            .orElseThrow { UserCouponNotFoundException() }
    }

    override fun findByUserId(userId: Long): List<UserCouponEntity> {
        return userCouponJpaRepository.findByUserId(userId)
    }

    override fun findByUserIdAndStatus(userId: Long, status: UserCouponStatus): List<UserCouponEntity> {
        return userCouponJpaRepository.findByUserIdAndStatus(userId, status)
    }

    override fun existsByUserIdAndCouponPolicyId(userId: Long, couponPolicyId: Long): Boolean {
        return userCouponJpaRepository.existsByUserIdAndCouponPolicyId(userId, couponPolicyId)
    }

    override fun findExpiredIssuedCoupons(now: LocalDateTime): List<UserCouponEntity> {
        return userCouponJpaRepository.findExpiredIssuedCoupons(now)
    }

    override fun saveAll(entities: List<UserCouponEntity>): List<UserCouponEntity> {
        return userCouponJpaRepository.saveAll(entities)
    }
}
