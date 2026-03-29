package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.product.domain.StockReservationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 만료된 예약 재고를 자동으로 해제하는 스케줄러
 *
 * - TTL 초과 예약 재고: RESERVED → EXPIRED 전환 + ProductDetail.release()
 * - 실행 주기: 매 5분
 */
@Component
class StockReservationExpiryScheduler(
    private val stockReservationService: StockReservationService
) {
    private val logger = LoggerFactory.getLogger(StockReservationExpiryScheduler::class.java)

    @Scheduled(fixedDelay = 5 * 60 * 1000L) // 5분마다 실행
    fun releaseExpiredReservations() {
        logger.info("STOCK:EXPIRY:SCHEDULER:START")
        try {
            stockReservationService.releaseExpired()
            logger.info("STOCK:EXPIRY:SCHEDULER:DONE")
        } catch (e: Exception) {
            logger.error("STOCK:EXPIRY:SCHEDULER:ERROR", e)
        }
    }
}
