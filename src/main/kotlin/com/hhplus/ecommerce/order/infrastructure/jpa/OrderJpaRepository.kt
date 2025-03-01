package com.hhplus.ecommerce.order.infrastructure.jpa

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderJpaRepository: JpaRepository<OrderEntity, Long> {
    fun findByIdAndStatus(id: Long, status: OrderStatus): Optional<OrderEntity>
}