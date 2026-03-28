package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OrderOutboxScheduler(
    private val outboxEventService: OutboxEventService
) {
    /** 매 1분마다 ORDER_STOCK_FAIL FAILED 이벤트를 재시도한다. */
    @Scheduled(cron = "0 */1 * * * *")
    fun retryOrderStockFail() {
        outboxEventService.processFailedOutbox("ORDER_STOCK_FAIL")
    }
}
