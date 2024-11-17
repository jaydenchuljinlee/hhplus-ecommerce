package com.hhplus.ecommerce.infrastructure.balance.event

import com.hhplus.ecommerce.infrastructure.balance.mongodb.BalanceHistoryDocument
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class BalanceKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, BalanceHistoryDocument>
):BalanceEventPublisher {
    private val TOPIC = "BALANCE_HISTORY"

    private val logger = LoggerFactory.getLogger(BalanceKafkaPublisher::class.java)

    override fun publish(event: BalanceHistoryDocument) {
        try {
            logger.info("BALANCE:KAFKA:PRODUCER: 이벤트 발행 요청 => $event")
            kafkaTemplate.send(TOPIC, event)
        }catch (e: Exception) {
            logger.info("BALANCE:KAFKA:PRODUCER:ERROR: 통신 실패 => ${e.message}")
        }
    }
}