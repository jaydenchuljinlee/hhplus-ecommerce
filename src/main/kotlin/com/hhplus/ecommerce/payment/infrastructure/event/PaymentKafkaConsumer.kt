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
        // 멱등성 보장: 이미 성공 처리된 이벤트는 재소비 시 스킵
        val outboxEvent = outboxEventRepository.findById(event.id)
        if (outboxEvent.status == OutboxEventStatus.SUCCESS) {
            logger.info("PAY:KAFKA:CONSUMER:SKIP 이미 처리된 이벤트: eventId={}", event.id)
            return
        }

        try {
            val payload = objectMapper.readValue(event.payload, PaymentHistoryDocument::class.java)

            logger.info("PAY:KAFKA:CONSUMER: {}", event)

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (payload.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            paymentHistoryMongoRepository.save(payload)

            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        } catch (e: Exception) {
            logger.error("PAY:KAFKA:CONSUMER:ERROR", e)

            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }

}