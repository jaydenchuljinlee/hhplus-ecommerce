package com.hhplus.ecommerce.schedular

import com.hhplus.ecommerce.domain.outboxevent.OutboxEventService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BalanceOutboxScheduler(
    private val outboxEventService: OutboxEventService
) {
    @Scheduled(cron = "0 */1 * * * *")
    fun retryPaymentHistory() {
        outboxEventService.processFailedOutbox("PAY_HISTORY")
    }
}