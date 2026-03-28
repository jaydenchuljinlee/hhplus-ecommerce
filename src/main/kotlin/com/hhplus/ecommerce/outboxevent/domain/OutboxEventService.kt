package com.hhplus.ecommerce.outboxevent.domain

import com.hhplus.ecommerce.outboxevent.domain.dto.OutboxResult
import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class OutboxEventService(
    private val outboxEventRepository: IOutboxEventRepository,
    private val kafkaProducer: KafkaProducer,
) {
    private val logger = LoggerFactory.getLogger(OutboxEventService::class.java)

    fun findById(id: UUID): OutboxResult {
        val result = outboxEventRepository.findById(id)
        return OutboxResult.from(result)
    }

    fun findAllByTopicStatusAndMaxRetryCnt(topic: String, status: OutboxEventStatus): List<OutboxResult> {
        val resultList = outboxEventRepository.findAllByTopicStatusAndMaxRetryCnt(topic, status)
        return resultList.map { OutboxResult.from(it) }
    }

    fun insertOrUpdate(event: OutboxEventEntity) {
        outboxEventRepository.insertOrUpdate(event)
    }

    fun increase(id: UUID) {
        val event = outboxEventRepository.findById(id)
        event.increaseRetryCnt()
        insertOrUpdate(event)
    }

    fun updateStatus(id: UUID, status: OutboxEventStatus) {
        val event = outboxEventRepository.findById(id)
        event.updateStatus(status)
        insertOrUpdate(event)
    }

    /**
     * FAILED 상태의 이벤트를 재시도한다.
     *
     * - 재시도 가능 이벤트 ([MAX_CNT] 이하): `retryCnt` 증가 후 Kafka 재발행
     * - 재시도 상한 초과 이벤트 ([MAX_CNT] 초과): WARN 로그 기록 (수동 처리 필요)
     */
    fun processFailedOutbox(topic: String) {
        val retryableList = findAllByTopicStatusAndMaxRetryCnt(topic, OutboxEventStatus.FAILED)
        retryableList.forEach {
            increase(it.id)
            kafkaProducer.sendOutboxEvent(it.toOutboxEventInfo())
        }

        warnExhaustedEvents(topic)
    }

    /**
     * 재시도 상한을 초과한 이벤트에 대해 WARN 로그를 기록한다.
     *
     * 이 이벤트들은 자동 재시도 대상에서 제외되므로 **수동 처리가 필요**하다.
     */
    private fun warnExhaustedEvents(topic: String) {
        val exhaustedList = outboxEventRepository.findExhaustedEvents(topic, OutboxEventStatus.FAILED)
        if (exhaustedList.isEmpty()) return

        exhaustedList.forEach { event ->
            logger.warn(
                "[수동 처리 필요] Outbox 이벤트 재시도 상한 초과: eventId={}, topic={}, retryCnt={}",
                event.id, event.topic, event.retryCnt
            )
        }
    }

}