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
        // 멱등성 보장: 이미 성공 처리된 이벤트는 재소비 시 스킵
        val outboxEvent = outboxEventRepository.findById(event.id)
        if (outboxEvent.status == OutboxEventStatus.SUCCESS) {
            logger.info("ORDER-STOCK-FAILED:KAFKA:CONSUMER:SKIP 이미 처리된 이벤트: eventId={}", event.id)
            return
        }

        try {
            val payload = objectMapper.readValue(event.payload, OrderStockFailEventResponse::class.java)
            logger.info("ORDER-STOCK-FAILED:KAFKA:CONSUMER orderId={}, productId={}", payload.orderId, payload.productId)

            val command = payload.toOrderDeletionCommand()
            orderService.deleteOrderDetail(command)

            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        } catch (e: Exception) {
            logger.error("ORDER-STOCK-FAILED:KAFKA:CONSUMER:ERROR eventId={}", event.id, e)

            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }
}
