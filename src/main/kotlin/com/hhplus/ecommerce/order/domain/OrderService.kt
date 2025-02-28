package com.hhplus.ecommerce.order.domain

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.dto.OrderCompleteCommand
import com.hhplus.ecommerce.order.domain.dto.OrderCreationCommand
import com.hhplus.ecommerce.order.domain.dto.OrderQuery
import com.hhplus.ecommerce.order.domain.dto.OrderResult
import com.hhplus.ecommerce.order.domain.repository.IOrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: IOrderRepository,
) {
    fun getOrder(query: OrderQuery): OrderResult {
        val entity = orderRepository.findByIdAndStatus(query.orderId, query.status)
        return OrderResult.from(entity)
    }

    fun order(item: OrderCreationCommand): OrderResult {
        val entity = orderRepository.insertOrUpdate(item.toEntity())
        return OrderResult.from(entity)
    }

    fun orderComplete(item: OrderCompleteCommand): OrderResult {
        val entity = orderRepository.findByIdAndStatus(item.orderId, "ORDER_REQUEST")
        entity.status = OrderStatus.CONFIRMED

        val updated = orderRepository.insertOrUpdate(entity)

        return OrderResult.from(updated)
    }
}