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
import org.springframework.transaction.annotation.Transactional

@Component
class OrderStockFailKafkaConsumer(
    private val outboxEventRepository: OutboxEventRepository,
    private val orderService: OrderService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(OrderStockFailKafkaConsumer::class.java)

    @Transactional
    @KafkaListener(
        groupId = "\${hhplus.kafka.order.group-id}",
        topics = ["\${hhplus.kafka.order.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        try {
            val payload = objectMapper.readValue(event.payload, OrderStockFailEventResponse::class.java)
            logger.info("ORDER-STOCK-FAILED:KAFKA:CONSUMER orderId=${payload.orderId}, productId=${payload.productId}")

            val command = payload.toOrderDeletionCommand()
            orderService.deleteOrderDetail(command)

        } catch (e: Exception) {
            logger.error("ORDER-STOCK-FAILED:KAFKA:CONSUMER:ERROR eventId=${event.id}", e)

            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }
}
