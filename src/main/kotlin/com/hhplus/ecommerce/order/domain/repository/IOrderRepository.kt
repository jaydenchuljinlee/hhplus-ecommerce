package com.hhplus.ecommerce.order.domain.repository

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderDetailEntity
import com.hhplus.ecommerce.order.infrastructure.jpa.entity.OrderEntity

interface IOrderRepository {
    fun findById(orderId: Long): OrderEntity
    fun findByIdAndStatus(orderId: Long, status: OrderStatus): OrderEntity
    fun findDetailsById(orderId: Long): List<OrderDetailEntity>

    fun insertOrUpdate(entity: OrderEntity): OrderEntity
    fun deleteOrderDetail(orderId: Long, productId: Long)
}