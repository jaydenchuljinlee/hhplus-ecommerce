package com.hhplus.ecommerce.domain.order.repository

import com.hhplus.ecommerce.infrastructure.order.jpa.entity.OrderEntity

interface IOrderRepository {
    fun findById(orderId: Long): OrderEntity
    fun findByIdAndStatus(orderId: Long, status: String): OrderEntity

    fun insertOrUpdate(entity: OrderEntity): OrderEntity
}