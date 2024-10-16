package com.hhplus.ecommerce.infrastructure.order.jpa

import com.hhplus.ecommerce.infrastructure.order.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderJpaRepository: JpaRepository<OrderEntity, Long> {
    fun findByIdAndStatus(id: Long, status: String): Optional<OrderEntity>
}