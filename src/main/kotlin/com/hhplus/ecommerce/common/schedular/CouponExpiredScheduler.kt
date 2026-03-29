package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.coupon.domain.CouponService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CouponExpiredScheduler(
    private val couponService: CouponService
) {
    private val logger = LoggerFactory.getLogger(CouponExpiredScheduler::class.java)

    @Scheduled(cron = "0 0 * * * *") // 매 시간
    fun expireOverdueCoupons() {
        val count = couponService.expireOverdueCoupons()
        if (count > 0) {
            logger.info("만료된 쿠폰 $count 건 처리 완료")
        }
    }
}
