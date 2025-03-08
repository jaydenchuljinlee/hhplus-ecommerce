package com.hhplus.ecommerce.order.infrastructure.jpa

import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderDetailEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderDetailRepository: JpaRepository<OrderDetailEntity, Long> {
    fun findByOrderIdAndProductId(orderId: Long, productId: Long): Optional<OrderDetailEntity>
}