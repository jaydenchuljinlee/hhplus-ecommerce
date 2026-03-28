package com.hhplus.ecommerce.common.schedular

import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BalanceOutboxScheduler(
    private val outboxEventService: OutboxEventService
) {
    /** 매 1분마다 BALANCE_HISTORY FAILED 이벤트를 재시도한다. */
    @Scheduled(cron = "0 */1 * * * *")
    fun retryBalanceHistory() {
        outboxEventService.processFailedOutbox("BALANCE_HISTORY")
    }
}