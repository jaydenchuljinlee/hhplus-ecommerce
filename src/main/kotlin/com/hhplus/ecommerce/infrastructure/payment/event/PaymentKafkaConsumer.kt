package com.hhplus.ecommerce.infrastructure.payment.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentKafkaConsumer(
    private val paymentHistoryMongoRepository: PaymentHistoryMongoRepository,
    private val objectMapper: ObjectMapper
): PaymentEventListener {
    private val logger = LoggerFactory.getLogger(PaymentKafkaConsumer::class.java)

    @KafkaListener(topics = ["PAY_HISTORY"])
    override fun listen(message: String) {
        try {
            val event = objectMapper.readValue(message, PaymentHistoryDocument::class.java)

            logger.info("PAY:KAFKA:CONSUMER: $event" )

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (event.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            paymentHistoryMongoRepository.save(event)
        } catch (e: Exception) {
            logger.error("PAY:KAFKA:CONSUMER:ERROR", e)
        }
    }

}