package com.hhplus.ecommerce.infrastructure.balance.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.infrastructure.balance.mongodb.BalanceHistoryDocument
import com.hhplus.ecommerce.infrastructure.balance.mongodb.BalanceHistoryMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BalanceKafkaConsumer(
    private val balanceHistoryMongoRepository: BalanceHistoryMongoRepository,
    private val objectMapper: ObjectMapper
): BalanceEventListener {
    private val logger = LoggerFactory.getLogger(BalanceKafkaConsumer::class.java)

    @KafkaListener(topics = ["BALANCE_HISTORY"])
    override fun listener(message: String) {
        try {
            val event = objectMapper.readValue(message, BalanceHistoryDocument::class.java)

            logger.info("BALANCE:KAFKA:CONSUMER: $event")

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (event.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            balanceHistoryMongoRepository.save(event)
        } catch(e: Exception) {
            logger.error("BALANCE:KAFKA:CONSUMER:ERROR", e)
        }
    }
}