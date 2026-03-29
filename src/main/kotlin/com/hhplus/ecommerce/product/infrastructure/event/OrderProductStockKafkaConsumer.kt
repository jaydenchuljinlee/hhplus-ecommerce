package com.hhplus.ecommerce.product.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.OrderStockFailKafkaProperties
import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.order.domain.dto.OrderStockConfirmCommand
import com.hhplus.ecommerce.order.domain.dto.OrderStockFailCommand
import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.product.infrastructure.dto.OrderDetailDeletionEventRequest
import com.hhplus.ecommerce.product.infrastructure.dto.OrderProductStockEventResponse
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderProductStockKafkaConsumer(
    private val productService: ProductService,
    private val stockReservationService: StockReservationService,
    private val orderService: OrderService,
    private val outboxEventService: OutboxEventService,
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaProducer: KafkaProducer,
    private val orderStockFailKafkaProperties: OrderStockFailKafkaProperties,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(OrderProductStockKafkaConsumer::class.java)

    @KafkaListener(
        groupId = "\${hhplus.kafka.product.group-id}",
        topics = ["\${hhplus.kafka.product.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        // 멱등성 보장: 이미 성공 처리된 이벤트는 재소비 시 스킵
        val outboxEvent = outboxEventRepository.findById(event.id)
        if (outboxEvent.status == OutboxEventStatus.SUCCESS) {
            logger.info("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER:SKIP 이미 처리된 이벤트: eventId={}", event.id)
            return
        }

        try {
            val payload = objectMapper.readValue(event.payload, OrderProductStockEventResponse::class.java)
            logger.info("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER: orderId={}", payload.orderId)

            val failedProductIds = mutableListOf<Long>()

            // 상품별 재고 Soft Reserve — availableQuantity 기준으로 점유
            payload.products.forEach { product ->
                try {
                    stockReservationService.reserve(
                        orderId = payload.orderId,
                        items = listOf(product.productId to product.quantity)
                    )
                    productService.deleteCache(product.productId)
                    logger.debug("PRODUCT-ORDER-STOCK:RESERVE:SUCCESS orderId={}, productId={}", payload.orderId, product.productId)
                } catch (e: OutOfStockException) {
                    logger.warn("PRODUCT-ORDER-STOCK:RESERVE:OUT_OF_STOCK orderId={}, productId={}", payload.orderId, product.productId)
                    failedProductIds.add(product.productId)
                    sendStockFailEvent(payload.orderId, product.productId)
                }
            }

            // 전체 결과에 따라 주문 상태 전이
            updateOrderStatus(payload.orderId, failedProductIds.size, payload.products.size)

            outboxEvent.updateStatus(OutboxEventStatus.SUCCESS)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        } catch (e: Exception) {
            logger.error("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER:ERROR eventId={}", event.id, e)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }

    /**
     * 재고 차감 결과에 따라 주문 상태 전이
     * - 전체 실패: REQUESTED → STOCK_FAILED
     * - 전체/부분 성공: REQUESTED → STOCK_CONFIRMED (실패 상품은 별도 이벤트로 OrderDetail 제거)
     */
    private fun updateOrderStatus(orderId: Long, failedCount: Int, totalCount: Int) {
        try {
            if (failedCount == totalCount) {
                logger.warn("PRODUCT-ORDER-STOCK:ALL_FAILED orderId={} → STOCK_FAILED", orderId)
                orderService.failStock(OrderStockFailCommand(orderId))
            } else {
                logger.info("PRODUCT-ORDER-STOCK:CONFIRMED orderId={} → STOCK_CONFIRMED (failed={}/{})", orderId, failedCount, totalCount)
                orderService.confirmStock(OrderStockConfirmCommand(orderId))
            }
        } catch (e: Exception) {
            logger.error("PRODUCT-ORDER-STOCK:STATUS_UPDATE:ERROR orderId={}", orderId, e)
        }
    }

    /** 재고 부족 상품의 OrderDetail 삭제 이벤트를 Kafka로 발행 */
    private fun sendStockFailEvent(orderId: Long, productId: Long) {
        val orderStockFailEvent = OrderDetailDeletionEventRequest.of(orderId, productId)
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = orderStockFailKafkaProperties.groupId,
            topic = orderStockFailKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(orderStockFailEvent)
        )

        val outboxEntity = outboxEvent.toEntity()
        outboxEntity.status = OutboxEventStatus.PUBLISH
        outboxEventService.insertOrUpdate(outboxEntity)

        kafkaProducer.sendOutboxEvent(outboxEvent)
    }
}
