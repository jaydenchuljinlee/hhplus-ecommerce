package com.hhplus.ecommerce.balance.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryDocument
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryMongoRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BalanceKafkaConsumer(
    private val balanceHistoryMongoRepository: BalanceHistoryMongoRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(BalanceKafkaConsumer::class.java)

    @KafkaListener(
        groupId = "\${hhplus.kafka.balance.group-id}",
        topics = ["\${hhplus.kafka.balance.topic}"])
    fun listener(event: OutboxEventInfo) {
        try {
            val payload = objectMapper.readValue(event.payload, BalanceHistoryDocument::class.java)

            logger.info("BALANCE:KAFKA:CONSUMER: $payload")

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (payload.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            balanceHistoryMongoRepository.save(payload)

            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        } catch(e: Exception) {
            logger.error("BALANCE:KAFKA:CONSUMER:ERROR", e)
            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }


}