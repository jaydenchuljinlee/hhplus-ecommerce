package com.hhplus.ecommerce.order.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.ProductStockKafkaProperties
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.notification.common.NotificationChannel
import com.hhplus.ecommerce.notification.common.NotificationType
import com.hhplus.ecommerce.notification.domain.INotificationEventPublisher
import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
import com.hhplus.ecommerce.order.usecase.dto.ProductStockEventRequest
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderFacade(
    private val userService: UserService,
    private val balanceService: BalanceService,
    private val orderService: OrderService,
    private val stockReservationService: StockReservationService,
    private val productService: ProductService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val productStockKafkaProperties: ProductStockKafkaProperties,
    private val objectMapper: ObjectMapper,
    private val notificationEventPublisher: INotificationEventPublisher,
) {
    private val logger = LoggerFactory.getLogger(OrderFacade::class.java)

    fun order(info: OrderCreation): OrderInfo {
        val user = userService.getUserById(info.toUserQuery())
        balanceService.validateBalanceToUse(info.toBalanceTransaction())

        // 주문 생성 — 자체 @Transactional로 커밋
        val order = orderService.order(info.toOrderCreationCommand())
        val result = OrderInfo.from(order)

        // 트랜잭션 밖에서 재고 점유 — 각 reserve()가 자체 @Transactional로 커밋
        try {
            info.details.forEach { detail ->
                val productDetail = productService.getProductDetail(detail.toProductDetailQuery())
                stockReservationService.reserve(result.orderId, productDetail.productDetailId, detail.quantity)
            }
        } catch (e: Exception) {
            logger.warn("재고 점유 실패 - orderId=${result.orderId}, 재고 해제 및 주문 취소 진행", e)
            stockReservationService.release(result.orderId)
            orderService.updateStatus(result.orderId, OrderStatus.CANCELED)
            throw e
        }

        // Kafka 이벤트 — 알림/후처리 목적
        val productEvent = ProductStockEventRequest.of(result)
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = productStockKafkaProperties.groupId,
            topic = productStockKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(productEvent),
            eventType = "OrderProductStock",
            schemaVersion = "1"
        )
        applicationEventPublisher.publishEvent(outboxEvent)

        notificationEventPublisher.publish(
            NotificationEvent(
                userId = user.userId,
                type = NotificationType.ORDER_PLACED,
                channel = NotificationChannel.PUSH,
                title = "주문이 접수되었습니다.",
                body = "주문 번호 ${result.orderId}번 주문이 정상적으로 접수되었습니다.",
                orderId = result.orderId
            )
        )

        return result
    }

    /**
     * Redisson 분산락 기반 주문 (성능 비교용)
     * reserve() — Lua 원자 스크립트 + Redis 재고
     * orderWithLock() — Redisson 분산락 + MySQL reservedQuantity
     */
    fun orderWithLock(info: OrderCreation): OrderInfo {
        val user = userService.getUserById(info.toUserQuery())
        balanceService.validateBalanceToUse(info.toBalanceTransaction())

        val order = orderService.order(info.toOrderCreationCommand())
        val result = OrderInfo.from(order)

        val reservedDetails = mutableListOf<Pair<Long, Int>>() // (productDetailId, quantity)
        try {
            info.details.forEach { detail ->
                val productDetail = productService.getProductDetail(detail.toProductDetailQuery())
                stockReservationService.reserveWithLock(result.orderId, productDetail.productDetailId, detail.quantity)
                reservedDetails.add(productDetail.productDetailId to detail.quantity)
            }
        } catch (e: Exception) {
            logger.warn("재고 점유 실패(lock) - orderId=${result.orderId}, 재고 해제 및 주문 취소 진행", e)
            reservedDetails.forEach { (productDetailId, quantity) ->
                stockReservationService.releaseByLock(productDetailId, quantity)
            }
            orderService.updateStatus(result.orderId, OrderStatus.CANCELED)
            throw e
        }

        val productEvent = ProductStockEventRequest.of(result)
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = productStockKafkaProperties.groupId,
            topic = productStockKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(productEvent),
            eventType = "OrderProductStock",
            schemaVersion = "1"
        )
        applicationEventPublisher.publishEvent(outboxEvent)

        notificationEventPublisher.publish(
            NotificationEvent(
                userId = user.userId,
                type = NotificationType.ORDER_PLACED,
                channel = NotificationChannel.PUSH,
                title = "주문이 접수되었습니다.",
                body = "주문 번호 ${result.orderId}번 주문이 정상적으로 접수되었습니다.",
                orderId = result.orderId
            )
        )

        return result
    }

}