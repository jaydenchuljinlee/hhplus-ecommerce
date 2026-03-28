package com.hhplus.ecommerce.product.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.OrderStockFailKafkaProperties
import com.hhplus.ecommerce.infrastructure.kafka.KafkaProducer
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.outboxevent.domain.OutboxEventService
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.dto.DecreaseProductDetailStock
import com.hhplus.ecommerce.product.infrastructure.dto.OrderDetailDeletionEventRequest
import com.hhplus.ecommerce.product.infrastructure.dto.OrderProductStockEventResponse
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class OrderProductStockKafkaConsumer(
    private var productService: ProductService,
    private val outboxEventService: OutboxEventService,
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
        val payload = objectMapper.readValue(event.payload, OrderProductStockEventResponse::class.java)

        logger.info("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER: $event")

        payload.products.forEach {
            try {
                val productDetailItem = DecreaseProductDetailStock.of(it)
                productService.decreaseStock(productDetailItem)
                productService.deleteCache(it.productId)
            } catch (e: OutOfStockException) {
                logger.warn("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER: 재고 부족으로 인한 OrderDetail 삭제 => orderId=${payload.orderId}, productId=${it.productId}")
                publishStockFailEvent(payload.orderId, it.productId)
            } catch (e: Exception) {
                logger.error("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER: 재고 감소 중 오류 발생 => orderId=${payload.orderId}, productId=${it.productId}", e)
                publishStockFailEvent(payload.orderId, it.productId)
            }
        }
    }

    private fun publishStockFailEvent(orderId: Long, productId: Long) {
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