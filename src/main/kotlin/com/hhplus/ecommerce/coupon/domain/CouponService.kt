package com.hhplus.ecommerce.coupon.domain

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.domain.dto.CouponIssueCommand
import com.hhplus.ecommerce.coupon.domain.dto.CouponPolicyResult
import com.hhplus.ecommerce.coupon.domain.dto.UserCouponResult
import com.hhplus.ecommerce.coupon.domain.repository.ICouponRepository
import com.hhplus.ecommerce.coupon.domain.repository.IUserCouponRepository
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponAlreadyUsedException
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponSoldOutException
import com.hhplus.ecommerce.coupon.infrastructure.jpa.entity.UserCouponEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 쿠폰 도메인 서비스
 *
 * issue() : @RedisLock 기반 선착순 발급 — 동시 요청 시 단 1건만 처리
 * use()   : 쿠폰 사용 처리 (주문 연결)
 * cancel(): 주문 취소 시 쿠폰 반납
 */
@Service
class CouponService(
    private val couponRepository: ICouponRepository,
    private val userCouponRepository: IUserCouponRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CouponService::class.java)
    }

    /**
     * 선착순 쿠폰 발급
     *
     * Redis 분산 락 + DB 비관적 락(이중 보호)으로 동시 발급 시 수량 초과 방지
     *
     * @throws CouponSoldOutException  수량 소진
     * @throws CouponAlreadyUsedException 이미 발급받은 쿠폰 (중복 방지)
     */
    @RedisLock(key = "'coupon:issue:' + #command.couponPolicyId")
    fun issue(command: CouponIssueCommand): UserCouponResult {
        // 중복 발급 방지
        if (userCouponRepository.existsByUserIdAndCouponPolicyId(command.userId, command.couponPolicyId)) {
            throw CouponAlreadyUsedException("이미 발급받은 쿠폰입니다.")
        }

        val policy = couponRepository.findByIdWithLock(command.couponPolicyId)

        // 발급 가능 기간 및 수량 검증
        if (!policy.isAvailable()) throw CouponSoldOutException("쿠폰 발급 기간이 아닙니다.")

        policy.issue()
        couponRepository.save(policy)

        val userCoupon = userCouponRepository.save(
            UserCouponEntity(
                userId = command.userId,
                couponPolicyId = command.couponPolicyId
            )
        )

        logger.info("COUPON:ISSUED userId={}, policyId={}, userCouponId={}", command.userId, command.couponPolicyId, userCoupon.id)
        return UserCouponResult.from(userCoupon)
    }

    /**
     * 쿠폰 사용 처리
     *
     * @param userCouponId 사용할 쿠폰 ID
     * @param orderId      연결할 주문 ID
     */
    @Transactional
    fun use(userCouponId: Long, orderId: Long): UserCouponResult {
        val userCoupon = userCouponRepository.findById(userCouponId)
        userCoupon.use(orderId)
        userCouponRepository.save(userCoupon)

        logger.info("COUPON:USED userCouponId={}, orderId={}", userCouponId, orderId)
        return UserCouponResult.from(userCoupon)
    }

    /**
     * 쿠폰 사용 취소 (주문 취소 시 보상)
     */
    @Transactional
    fun cancel(userCouponId: Long): UserCouponResult {
        val userCoupon = userCouponRepository.findById(userCouponId)
        userCoupon.cancel()
        userCouponRepository.save(userCoupon)

        logger.info("COUPON:CANCELLED userCouponId={}", userCouponId)
        return UserCouponResult.from(userCoupon)
    }

    /** 사용자 쿠폰 목록 조회 */
    @Transactional(readOnly = true)
    fun getUserCoupons(userId: Long): List<UserCouponResult> {
        return userCouponRepository.findByUserId(userId).map { UserCouponResult.from(it) }
    }

    /** 사용 가능한 쿠폰만 조회 */
    @Transactional(readOnly = true)
    fun getAvailableCoupons(userId: Long): List<UserCouponResult> {
        return userCouponRepository.findByUserIdAndStatus(userId, UserCouponStatus.ISSUED)
            .map { UserCouponResult.from(it) }
    }

    /** 쿠폰 정책 조회 */
    @Transactional(readOnly = true)
    fun getCouponPolicy(couponPolicyId: Long): CouponPolicyResult {
        return CouponPolicyResult.from(couponRepository.findById(couponPolicyId))
    }

    /**
     * 만료 쿠폰 상태 자동 업데이트 (스케줄러에서 호출)
     */
    @Transactional
    fun expireOverdueCoupons() {
        val now = LocalDateTime.now()
        val expiredCoupons = userCouponRepository.findExpiredIssuedCoupons(now)

        if (expiredCoupons.isEmpty()) return

        expiredCoupons.forEach { it.expire() }
        userCouponRepository.saveAll(expiredCoupons)

        logger.info("COUPON:EXPIRED count={}", expiredCoupons.size)
    }
}
