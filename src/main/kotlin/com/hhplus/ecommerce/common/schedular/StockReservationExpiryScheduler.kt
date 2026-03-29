package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.product.domain.StockReservationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class StockReservationExpiryScheduler(
    private val stockReservationService: StockReservationService
) {
    private val logger = LoggerFactory.getLogger(StockReservationExpiryScheduler::class.java)

    @Scheduled(cron = "0 */5 * * * *")
    fun expireOverdueReservations() {
        val count = stockReservationService.expireOverdue()
        if (count > 0) logger.info("만료된 예약 재고 $count 건 처리 완료")
    }
}
