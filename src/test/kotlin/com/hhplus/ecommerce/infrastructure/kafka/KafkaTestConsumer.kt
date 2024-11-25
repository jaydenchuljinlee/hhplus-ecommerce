package com.hhplus.ecommerce.infrastructure.kafka

import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Component
class KafkaTestConsumer {
    var message: String? = null
    private val latch = CountDownLatch(1)


    @KafkaListener(topics = ["BALANCE_HISTORY_TEST"], groupId = "OUTBOX_TEST")
    fun listen(event: OutboxEventInfo) {
        println("message => $${event.topic}")
        this.message = event.topic
        latch.countDown() // 메시지 처리 완료 신호
    }

    fun waitForMessage(timeout: Long, unit: TimeUnit): Boolean {
        return latch.await(timeout, unit)
    }
}