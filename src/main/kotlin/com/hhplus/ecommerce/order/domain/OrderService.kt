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
    fun order(command: OrderCreationCommand): OrderResult {
        val entity = command.toEntity()
        val details = command.details.map { it.toEntity(entity) }
        entity.orderDetails.addAll(details)
        orderRepository.insertOrUpdate(entity)
        return OrderResult.from(entity)
    }

    @Transactional
    fun orderComplete(command: OrderCompleteCommand) {
        val entity = orderRepository.findByIdAndStatus(command.orderId, OrderStatus.REQUESTED)
        entity.status = OrderStatus.CONFIRMED

        orderRepository.insertOrUpdate(entity)
    }

    fun deleteOrderDetail(command: OrderDeletionCommand) {
        orderRepository.deleteOrderDetail(command.orderId, command.productId)
    }
}