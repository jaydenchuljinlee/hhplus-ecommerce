package com.hhplus.ecommerce.order.infrastructure

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.exception.OrderNotFoundException
import com.hhplus.ecommerce.order.domain.repository.IOrderRepository
import com.hhplus.ecommerce.order.infrastructure.jpa.OrderDetailRepository
import com.hhplus.ecommerce.order.infrastructure.jpa.OrderJpaRepository
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderDetailEntity
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OrderRepository(
    private val orderJpaRepository: OrderJpaRepository
): IOrderRepository {
    override fun findById(orderId: Long): OrderEntity {
        return orderJpaRepository.findById(orderId).orElseThrow { OrderNotFoundException() }
    }

    override fun findByIdAndStatus(orderId: Long, status: OrderStatus): OrderEntity {
        return orderJpaRepository.findByIdAndStatus(orderId, status).orElseThrow { OrderNotFoundException() }
    }

    override fun findDetailsById(orderId: Long): List<OrderDetailEntity> {
        val order = this.findById(orderId)
        return order.orderDetails
    }

    override fun insertOrUpdate(entity: OrderEntity): OrderEntity {
        return orderJpaRepository.save(entity)
    }

    @Transactional
    override fun deleteOrderDetail(orderId: Long, productId: Long) {
        val entity = this.findById(orderId)
        entity.removeOf(productId)
        orderJpaRepository.save(entity)
    }
}