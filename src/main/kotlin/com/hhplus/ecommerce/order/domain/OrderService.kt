package com.hhplus.ecommerce.order.domain

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.dto.*
import com.hhplus.ecommerce.order.domain.repository.IOrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: IOrderRepository
) {
    fun getOrder(query: OrderQuery): OrderResult {
        val order = orderRepository.findByIdAndStatus(query.orderId, query.status)
        return OrderResult.from(order)
    }

    @Transactional
    fun order(item: OrderCreationCommand): OrderResult {
        val entity = item.toEntity()
        val details = item.details.map { it.toEntity(entity) }
        entity.orderDetails.addAll(details)
        orderRepository.insertOrUpdate(entity)
        return OrderResult.from(entity)
    }

    fun orderComplete(item: OrderCompleteCommand) {
        val entity = orderRepository.findByIdAndStatus(item.orderId, OrderStatus.REQUESTED)
        entity.status = OrderStatus.CONFIRMED

        orderRepository.insertOrUpdate(entity)
    }
}