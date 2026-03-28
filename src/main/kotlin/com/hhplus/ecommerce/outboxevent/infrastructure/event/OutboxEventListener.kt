package com.hhplus.ecommerce.outboxevent.infrastructure.event

import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OutboxEventListener(
    private val outboxEventService: OutboxEventService,
    private val kafkaProducer: KafkaProducer
) {
    private val logger = LoggerFactory.getLogger(OutboxEventListener::class.java)

    /**
     * [1단계] 트랜잭션 커밋 직전에 Outbox 레코드를 DB에 저장한다.
     *
     * - BEFORE_COMMIT: 외부 트랜잭션과 동일한 커밋/롤백 단위로 묶임
     *   → 비즈니스 로직 실패 시 Outbox 레코드도 함께 롤백됨 (정합성 보장)
     * - @EventListener 대신 사용하는 이유:
     *   @EventListener는 트랜잭션 유무와 관계없이 즉시 실행되어
     *   트랜잭션 없이 publishEvent()가 호출될 경우 DB 정합성이 깨질 수 있음
     */
    @Order(1)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleOutboxEvent(event: OutboxEventInfo) {
        logger.info("OUTBOX_EVENT:LISTENER:BEFORE_COMMIT: {}", event)
        outboxEventService.insertOrUpdate(event.toEntity())
    }

    /**
     * [2단계] 트랜잭션 커밋 완료 후 비동기로 Kafka에 이벤트를 발행한다.
     *
     * - @Async: 별도 스레드에서 실행 — 원본 트랜잭션이 이미 커밋되어 트랜잭션 컨텍스트 없음
     * - @Transactional(REQUIRES_NEW): 비동기 스레드에서 Outbox 상태 갱신을 위한 신규 트랜잭션 시작
     *   → updateStatus()가 트랜잭션 없이 실행될 경우 JPA TransactionRequiredException 방지
     * - Kafka 발행 실패 시 Outbox 상태를 FAILED로 기록 → 스케줄러 재시도 대상이 됨
     */
    @Async
    @Order(2)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publish(event: OutboxEventInfo) {
        try {
            logger.info("OUTBOX_EVENT:LISTENER:AFTER_COMMIT {}", event)
            outboxEventService.updateStatus(event.id, OutboxEventStatus.PUBLISH)
            kafkaProducer.sendOutboxEvent(event)
        } catch (e: Exception) {
            logger.error("OUTBOX_EVENT:LISTENER:AFTER_COMMIT:FAILED => event_id {}", event.id, e)
            outboxEventService.updateStatus(event.id, OutboxEventStatus.FAILED)
        }
    }
}