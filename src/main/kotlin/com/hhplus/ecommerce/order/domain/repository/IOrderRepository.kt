package com.hhplus.ecommerce.order.domain.repository

import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

interface IOrderRepository {
    fun findById(orderId: Long): OrderEntity
    fun findByIdAndStatus(orderId: Long, status: String): OrderEntity

    fun insertOrUpdate(entity: OrderEntity): OrderEntity
}