package com.hhplus.ecommerce.order.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.infrastructure.dto.OrderStockFailEventResponse
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderStockFailKafkaConsumer(
    private val outboxEventRepository: OutboxEventRepository,
    private var orderService: OrderService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(OrderStockFailKafkaConsumer::class.java)

    companion object {
        private val SUPPORTED_VERSIONS = setOf("1")
    }

    @KafkaListener(
        groupId = "\${hhplus.kafka.order.group-id}",
        topics = ["\${hhplus.kafka.order.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        if (event.schemaVersion !in SUPPORTED_VERSIONS) {
            logger.warn("ORDER-STOCK-FAILED:KAFKA:CONSUMER:UNSUPPORTED_VERSION - version=${event.schemaVersion}, eventId=${event.id}")
            return
        }
        try {
            logger.info("ORDER-STOCK-FAILED:KAFKA:CONSUMER: $event")
            val payload = objectMapper.readValue(event.payload, OrderStockFailEventResponse::class.java)
            val command = payload.toOrderDeletionCommand()
            orderService.deleteOrderDetail(command)
        } catch (e: Exception) {
            logger.error("ORDER-STOCK-FAILED:KAFKA:CONSUMER:ERROR", e)

            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }
}