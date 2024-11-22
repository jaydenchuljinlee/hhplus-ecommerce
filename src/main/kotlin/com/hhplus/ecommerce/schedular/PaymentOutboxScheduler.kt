package com.hhplus.ecommerce.schedular

import com.hhplus.ecommerce.domain.outboxevent.OutboxEventService
import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PaymentOutboxScheduler(
    private val outboxEventService: OutboxEventService,
    private val kafkaProducer: KafkaProducer,
) {
    @Scheduled(cron = "0 */1 * * * *")
    fun retryPaymentHistory() {
        outboxEventService.processFailedOutbox("BALANCE_HISTORY")
    }
}