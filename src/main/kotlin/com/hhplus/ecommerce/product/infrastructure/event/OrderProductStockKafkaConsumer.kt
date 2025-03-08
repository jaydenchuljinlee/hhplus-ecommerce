package com.hhplus.ecommerce.product.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.dto.DecreaseProductDetailStock
import com.hhplus.ecommerce.product.infrastructure.dto.OrderProductStockEventResponse
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderProductStockKafkaConsumer(
    private var productService: ProductService,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(OrderProductStockKafkaConsumer::class.java)

    @KafkaListener(
        groupId = "\${hhplus.kafka.product.group-id}",
        topics = ["\${hhplus.kafka.product.topic}"]
    )
    fun listen(event: OutboxEventInfo) {
        try {
            val payload = objectMapper.readValue(event.payload, OrderProductStockEventResponse::class.java)

            logger.info("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER: $event")

            // TODO 재고 감소 목록 중에 특정 품목이 실패할 경우, 해당 품목을 주문 정보에서 제거(가격 정보도 제거 필요)
            // 상품 정보 재고 감소
            payload.products.forEach {
                val productDetailItem = DecreaseProductDetailStock(
                    id = it.productId,
                    amount = it.quantity,
                )
                productService.decreaseStock(productDetailItem)
                productService.deleteCache(it.productId)
            }
        } catch (e: Exception) {
            logger.error("PRODUCT-ORDER-STOCK:KAFKA:CONSUMER:ERROR", e)

            val outboxEvent = outboxEventRepository.findById(event.id)
            outboxEvent.updateStatus(OutboxEventStatus.FAILED)
            outboxEventRepository.insertOrUpdate(outboxEvent)
        }
    }
}