package com.hhplus.ecommerce.payment.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import com.hhplus.ecommerce.payment.infrastructure.mongodb.PaymentHistoryDocument
import com.hhplus.ecommerce.payment.infrastructure.mongodb.PaymentHistoryMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentKafkaConsumer(
    private val paymentHistoryMongoRepository: PaymentHistoryMongoRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(PaymentKafkaConsumer::class.java)

    @KafkaListener(
        groupId = "\${hhplus.kafka.payment.group-id}",
        topics = ["\${hhplus.kafka.payment.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        try {
            val payload = objectMapper.readValue(event.payload, PaymentHistoryDocument::class.java)

            logger.info("PAY:KAFKA:CONSUMER: $event" )

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (payload.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            paymentHistoryMongoRepository.save(payload)

            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        } catch (e: Exception) {
            logger.error("PAY:KAFKA:CONSUMER:ERROR", e)

            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }

}