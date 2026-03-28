package com.hhplus.ecommerce.order.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.common.properties.ProductStockKafkaProperties
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.user.domain.UserService
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderInfo
import com.hhplus.ecommerce.order.usecase.dto.ProductStockEventRequest
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class OrderFacade(
    private val userService: UserService,
    private val balanceService: BalanceService,
    private val orderService: OrderService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val productStockKafkaProperties: ProductStockKafkaProperties,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    fun order(info: OrderCreation): OrderInfo {
        val user = userService.getUserById(info.toUserQuery())
        balanceService.validateBalanceToUse(info.toBalanceTransaction())

        val order = orderService.order(info.toOrderCreationCommand())
        val result = OrderInfo.from(order)

        val productEvent = ProductStockEventRequest.of(result)

        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = productStockKafkaProperties.groupId,
            topic = productStockKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(productEvent)
        )

        applicationEventPublisher.publishEvent(outboxEvent)

        return result
    }

}
