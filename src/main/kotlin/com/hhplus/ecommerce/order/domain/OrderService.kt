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

    /** 재고 확보 완료: REQUESTED → STOCK_CONFIRMED */
    @Transactional
    fun confirmStock(command: OrderStockConfirmCommand) {
        val entity = orderRepository.findByIdAndStatus(command.orderId, OrderStatus.REQUESTED)
        entity.confirmStock()
        orderRepository.insertOrUpdate(entity)
    }

    /** 재고 확보 실패: REQUESTED → STOCK_FAILED */
    @Transactional
    fun failStock(command: OrderStockFailCommand) {
        val entity = orderRepository.findByIdAndStatus(command.orderId, OrderStatus.REQUESTED)
        entity.failStock()
        orderRepository.insertOrUpdate(entity)
    }

    /** 결제 완료 확정: STOCK_CONFIRMED → CONFIRMED */
    @Transactional
    fun orderComplete(command: OrderCompleteCommand) {
        val entity = orderRepository.findByIdAndStatus(command.orderId, OrderStatus.STOCK_CONFIRMED)
        entity.confirm()
        orderRepository.insertOrUpdate(entity)
    }

    fun deleteOrderDetail(command: OrderDeletionCommand) {
        orderRepository.deleteOrderDetail(command.orderId, command.productId)
    }
}
