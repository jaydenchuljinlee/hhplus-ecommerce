package com.hhplus.ecommerce.order.infrastructure.jpa

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface OrderJpaRepository: JpaRepository<OrderEntity, Long> {
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderDetails WHERE o.id = :id AND o.status = :status")
    fun findByIdAndStatus(id: Long, status: OrderStatus): Optional<OrderEntity>

    fun findAllByStatus(status: OrderStatus): List<OrderEntity>

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status WHERE o.id = :id")
    fun updateStatusById(@Param("id") id: Long, @Param("status") status: OrderStatus)
}