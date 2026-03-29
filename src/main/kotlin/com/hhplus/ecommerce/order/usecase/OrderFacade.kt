package com.hhplus.ecommerce.order.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.ProductStockKafkaProperties
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.notification.domain.event.INotificationEventPublisher
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
import com.hhplus.ecommerce.order.usecase.dto.ProductStockEventRequest
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class OrderFacade(
    private val userService: UserService,
    private val orderService: OrderService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val productStockKafkaProperties: ProductStockKafkaProperties,
    private val notificationEventPublisher: INotificationEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(OrderFacade::class.java)
    }

    @Transactional
    fun order(info: OrderCreation): OrderInfo {
        val user = userService.getUserById(info.toUserQuery())

        val order = orderService.order(info.toOrderCreationCommand())
        val result = OrderInfo.from(order)

        // 재고 차감 이벤트 발행 (Outbox)
        val productEvent = ProductStockEventRequest.of(result)
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = productStockKafkaProperties.groupId,
            topic = productStockKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(productEvent)
        )
        applicationEventPublisher.publishEvent(outboxEvent)

        // 주문 접수 알림 발행 (Outbox → Kafka → NotificationKafkaConsumer)
        runCatching {
            notificationEventPublisher.publish(
                NotificationEvent.orderPlaced(userId = info.userId, orderId = result.orderId)
            )
        }.onFailure { e ->
            logger.warn("ORDER_PLACED 알림 발행 실패 (비필수) orderId={}", result.orderId, e)
        }

        return result
    }

}
