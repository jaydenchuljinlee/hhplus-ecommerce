package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.coupon.domain.CouponService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 만료된 쿠폰 상태를 자동으로 EXPIRED로 전환하는 스케줄러
 *
 * 실행 주기: 매일 새벽 2시
 */
@Component
class CouponExpiredScheduler(
    private val couponService: CouponService
) {
    private val logger = LoggerFactory.getLogger(CouponExpiredScheduler::class.java)

    @Scheduled(cron = "0 0 2 * * *") // 매일 02:00
    fun expireOverdueCoupons() {
        logger.info("COUPON:EXPIRY:SCHEDULER:START")
        try {
            couponService.expireOverdueCoupons()
            logger.info("COUPON:EXPIRY:SCHEDULER:DONE")
        } catch (e: Exception) {
            logger.error("COUPON:EXPIRY:SCHEDULER:ERROR", e)
        }
    }
}
