package com.hhplus.ecommerce.order.infrastructure.jpa.entity

import com.hhplus.ecommerce.order.common.OrderStatus
import jakarta.persistence.*

@Entity
@Table(name = "order_detail")
class OrderDetailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "order_id")
    var orderId: Long,
    @Column(name = "product_id")
    var productId: Long,
    @Column(name = "quantity")
    var quantity: Int,
    @Column(name = "price")
    var price: Long
)