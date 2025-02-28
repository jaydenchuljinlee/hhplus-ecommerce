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

    fun processFailedOutbox(topic: String) {
        val list = findAllByTopicStatusAndMaxRetryCnt(topic, OutboxEventStatus.FAILED)
        list.forEach {
            increase(it.id)
            kafkaProducer.sendOutboxEvent(it.toOutboxEventInfo())
        }
    }

}