package com.hhplus.ecommerce.schedular

import com.hhplus.ecommerce.infrastructure.outboxevent.OutboxEventRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.event.OutboxEventKafkaProducer
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.enums.OutboxEventStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PaymentOutboxScheduler(
    private val outboxEventRepository: OutboxEventRepository,
    private val outboxEventKafkaProducer: OutboxEventKafkaProducer,
) {
    @Scheduled(cron = "0 */1 * * * *")
    fun retryPaymentHistory() {
        val list = outboxEventRepository.findAllByTopicStatusAndMaxRetryCnt("BALANCE_HISTORY", OutboxEventStatus.FAILED)

        list.forEach {
            it.increaseRetryCnt()
            outboxEventKafkaProducer.afterCommit(it)
        }
    }
}