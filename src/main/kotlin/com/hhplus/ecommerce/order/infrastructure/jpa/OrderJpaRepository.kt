package com.hhplus.ecommerce.order.infrastructure.jpa

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface OrderJpaRepository: JpaRepository<OrderEntity, Long> {
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderDetails WHERE o.id = :id AND o.status = :status")
    fun findByIdAndStatus(id: Long, status: OrderStatus): Optional<OrderEntity>
}