package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.shipment.domain.ShipmentService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PurchaseConfirmScheduler(
    private val shipmentService: ShipmentService
) {
    private val logger = LoggerFactory.getLogger(PurchaseConfirmScheduler::class.java)

    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시
    fun confirmPurchase() {
        val threshold = LocalDateTime.now().minusDays(7)
        val count = shipmentService.confirmPurchaseForDeliveredBefore(threshold)
        if (count > 0) {
            logger.info("배송 완료 7일 경과 자동 구매 확정 처리: $count 건")
        }
    }
}
