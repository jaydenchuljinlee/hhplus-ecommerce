package com.hhplus.ecommerce.infrastructure.payment.event

import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, PaymentHistoryDocument>
): PaymentEventPublisher {
    private val TOPIC = "PAY_HISTORY"
    private val logger = LoggerFactory.getLogger(PaymentKafkaPublisher::class.java)

    override fun publish(paymentHistoryDocument: PaymentHistoryDocument) {
        try {
            logger.info("PAYMENT:KAFKA:PRODUCER: 이벤트 발행 요청 =>  $paymentHistoryDocument")
            kafkaTemplate.send(TOPIC, paymentHistoryDocument)
        } catch (e: Exception) {
            logger.info("PAYMENT:KAFKA:PRODUCER:ERROR => ${e.message}")
        }
    }

}