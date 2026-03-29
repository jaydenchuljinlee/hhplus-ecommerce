package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.shipment.domain.ShipmentService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 배송 완료 7일 후 자동 구매 확정 스케줄러
 *
 * 고객이 별도로 구매 확정을 하지 않아도 배송 완료 7일 후
 * 자동으로 PURCHASE_CONFIRMED 상태로 전환한다.
 *
 * 실행 주기: 매일 새벽 1시
 */
@Component
class PurchaseConfirmScheduler(
    private val shipmentService: ShipmentService
) {
    private val logger = LoggerFactory.getLogger(PurchaseConfirmScheduler::class.java)

    @Scheduled(cron = "0 0 1 * * *") // 매일 01:00
    fun autoConfirmPurchase() {
        logger.info("PURCHASE_CONFIRM:SCHEDULER:START")
        try {
            shipmentService.autoConfirmPurchase()
            logger.info("PURCHASE_CONFIRM:SCHEDULER:DONE")
        } catch (e: Exception) {
            logger.error("PURCHASE_CONFIRM:SCHEDULER:ERROR", e)
        }
    }
}
