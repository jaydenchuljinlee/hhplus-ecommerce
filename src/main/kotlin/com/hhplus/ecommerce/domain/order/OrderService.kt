package com.hhplus.ecommerce.domain.order

import com.hhplus.ecommerce.domain.order.dto.OrderCompleteCommand
import com.hhplus.ecommerce.domain.order.dto.OrderCreationCommand
import com.hhplus.ecommerce.domain.order.dto.OrderQuery
import com.hhplus.ecommerce.domain.order.dto.OrderResult
import com.hhplus.ecommerce.domain.order.repository.IOrderRepository
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
        entity.status = "ORDER_COMPLETE"

        val updated = orderRepository.insertOrUpdate(entity)

        return OrderResult.from(updated)
    }
}