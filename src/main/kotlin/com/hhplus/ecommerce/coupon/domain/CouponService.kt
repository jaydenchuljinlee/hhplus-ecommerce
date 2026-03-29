package com.hhplus.ecommerce.coupon.domain

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.domain.dto.UserCouponResult
import com.hhplus.ecommerce.coupon.domain.repository.ICouponPolicyRepository
import com.hhplus.ecommerce.coupon.domain.repository.IUserCouponRepository
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponAlreadyIssuedException
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponNotFoundException
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponOutOfStockException
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponPolicyRepository: ICouponPolicyRepository,
    private val userCouponRepository: IUserCouponRepository
) {
    @RedisLock(key = "'coupon:' + #couponPolicyId")
    fun issue(userId: Long, couponPolicyId: Long): UserCouponResult {
        val policy = couponPolicyRepository.findByIdWithLock(couponPolicyId)

        if (!policy.isAvailable()) {
            throw CouponOutOfStockException("쿠폰이 만료되었거나 재고가 없습니다. id=$couponPolicyId")
        }

        val existing = userCouponRepository.findByUserIdAndCouponPolicyId(userId, couponPolicyId)
        if (existing != null) {
            throw CouponAlreadyIssuedException("이미 발급된 쿠폰입니다. userId=$userId, couponPolicyId=$couponPolicyId")
        }

        policy.issue()
        couponPolicyRepository.save(policy)

        val userCoupon = UserCouponEntity(
            userId = userId,
            couponPolicyId = couponPolicyId
        )
        val saved = userCouponRepository.save(userCoupon)
        return UserCouponResult.from(saved)
    }

    @Transactional
    fun use(userCouponId: Long, orderId: Long): Long {
        val userCoupon = userCouponRepository.findById(userCouponId)
        val policy = couponPolicyRepository.findById(userCoupon.couponPolicyId)

        userCoupon.use(orderId)
        userCouponRepository.save(userCoupon)

        return policy.calculateDiscount(0L) // 실제 주문 금액은 호출자에서 전달
    }

    @Transactional
    fun useWithAmount(userCouponId: Long, orderId: Long, orderAmount: Long): Long {
        val userCoupon = userCouponRepository.findById(userCouponId)
        val policy = couponPolicyRepository.findById(userCoupon.couponPolicyId)

        userCoupon.use(orderId)
        userCouponRepository.save(userCoupon)

        return policy.calculateDiscount(orderAmount)
    }

    @Transactional
    fun cancel(userCouponId: Long) {
        val userCoupon = userCouponRepository.findById(userCouponId)
        userCoupon.cancel()
        userCouponRepository.save(userCoupon)
    }

    @Transactional(readOnly = true)
    fun getMyCoupons(userId: Long): List<UserCouponResult> =
        userCouponRepository.findByUserId(userId).map { UserCouponResult.from(it) }

    @Transactional
    fun expireOverdueCoupons(): Int {
        val now = LocalDateTime.now()
        val expiredPolicies = couponPolicyRepository.findExpiredPolicies(now)
        if (expiredPolicies.isEmpty()) return 0

        val expiredPolicyIds = expiredPolicies.map { it.id }
        val issuedCoupons = userCouponRepository.findIssuedByPolicyIds(expiredPolicyIds)

        issuedCoupons.forEach { it.expire() }
        userCouponRepository.saveAll(issuedCoupons)

        return issuedCoupons.size
    }
}
