package com.hhplus.ecommerce.infrastructure.order

import com.hhplus.ecommerce.common.exception.order.OrderNotFoundException
import com.hhplus.ecommerce.domain.order.repository.IOrderRepository
import com.hhplus.ecommerce.infrastructure.order.jpa.OrderJpaRepository
import com.hhplus.ecommerce.infrastructure.order.jpa.entity.OrderEntity
import org.springframework.stereotype.Repository

@Repository
class OrderRepository(
    private val orderJpaRepository: OrderJpaRepository
): IOrderRepository {
    override fun findById(orderId: Long): OrderEntity {
        return orderJpaRepository.findById(orderId).orElseThrow { OrderNotFoundException() }
    }

    override fun findByIdAndStatus(orderId: Long, status: String): OrderEntity {
        return orderJpaRepository.findByIdAndStatus(orderId, status).orElseThrow { OrderNotFoundException() }
    }

    override fun insertOrUpdate(entity: OrderEntity): OrderEntity {
        return orderJpaRepository.save(entity)
    }
}