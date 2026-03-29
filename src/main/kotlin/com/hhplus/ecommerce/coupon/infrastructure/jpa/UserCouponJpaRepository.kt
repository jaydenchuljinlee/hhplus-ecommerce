package com.hhplus.ecommerce.coupon.infrastructure.jpa

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserCouponJpaRepository : JpaRepository<UserCouponEntity, Long> {
    fun findByUserId(userId: Long): List<UserCouponEntity>
    fun findByUserIdAndStatus(userId: Long, status: UserCouponStatus): List<UserCouponEntity>
    fun existsByUserIdAndCouponPolicyId(userId: Long, couponPolicyId: Long): Boolean

    /** 만료 처리 대상: ISSUED 상태이고 쿠폰 정책의 expiredAt이 지난 쿠폰 */
    @Query("""
        SELECT uc FROM UserCouponEntity uc
        JOIN CouponPolicyEntity cp ON uc.couponPolicyId = cp.id
        WHERE uc.status = 'ISSUED'
          AND cp.expiredAt < :now
    """)
    fun findExpiredIssuedCoupons(@Param("now") now: LocalDateTime): List<UserCouponEntity>
}
