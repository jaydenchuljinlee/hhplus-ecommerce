package com.hhplus.ecommerce.product.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.infrastructure.dto.OrderProductStockEventResponse
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderProductStockKafkaConsumer(
    private val productService: ProductService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(OrderProductStockKafkaConsumer::class.java)

    companion object {
        private val SUPPORTED_VERSIONS = setOf("1")
    }

    @KafkaListener(
        groupId = "\${hhplus.kafka.product.group-id}",
        topics = ["\${hhplus.kafka.product.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        if (event.schemaVersion !in SUPPORTED_VERSIONS) {
            logger.warn("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER:UNSUPPORTED_VERSION - version=${event.schemaVersion}, eventId=${event.id}")
            return
        }

        val payload = objectMapper.readValue(event.payload, OrderProductStockEventResponse::class.java)

        logger.info("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER: orderId=${payload.orderId}")

        // 재고 점유는 OrderFacade에서 동기적으로 처리됨 — 캐시만 무효화
        payload.products.forEach {
            productService.deleteCache(it.productId)
        }
    }
}