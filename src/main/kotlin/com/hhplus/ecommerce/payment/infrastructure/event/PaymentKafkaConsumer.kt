package com.hhplus.ecommerce.payment.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import com.hhplus.ecommerce.payment.infrastructure.mongodb.PaymentHistoryDocument
import com.hhplus.ecommerce.payment.infrastructure.mongodb.PaymentHistoryMongoRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class PaymentKafkaConsumer(
    private val paymentHistoryMongoRepository: PaymentHistoryMongoRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(PaymentKafkaConsumer::class.java)

    companion object {
        private val SUPPORTED_VERSIONS = setOf("1")
    }

    @KafkaListener(
        groupId = "\${hhplus.kafka.payment.group-id}",
        topics = ["\${hhplus.kafka.payment.topic}"]
    )
    fun listen(event: OutboxEventInfo, ack: Acknowledgment) {  // Q68: manual commit
        if (event.schemaVersion !in SUPPORTED_VERSIONS) {
            logger.warn("PAY:KAFKA:CONSUMER:UNSUPPORTED_VERSION - version=${event.schemaVersion}, eventId=${event.id}")
            ack.acknowledge()
            return
        }

        // Q64: eventId 기반 멱등성 검증 — 이미 처리된 이벤트 중복 방지
        val outboxEvent = outboxEventRepository.findById(event.id)
        if (outboxEvent.status == OutboxEventStatus.SUCCESS) {
            logger.warn("PAY:KAFKA:CONSUMER:DUPLICATE - eventId=${event.id}, skipping")
            ack.acknowledge()
            return
        }

        try {
            val payload = objectMapper.readValue(event.payload, PaymentHistoryDocument::class.java)

            logger.info("PAY:KAFKA:CONSUMER: $event")

            // 에러가 발생해도 잘 동작하는지 테스트하기 위함
            if (payload.id == "ERROR") {
                throw IllegalArgumentException("ID가 올바르지 않습니다.")
            }

            // 외부 MongoDB에 이력 데이터를 저장
            paymentHistoryMongoRepository.save(payload)

            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
            ack.acknowledge()  // Q68: 처리 완료 후 offset commit
        } catch (e: Exception) {
            logger.error("PAY:KAFKA:CONSUMER:ERROR", e)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
            // ack 미호출 → DLQ ErrorHandler가 재시도 후 DLT로 이동 (Q65)
        }
    }
}
