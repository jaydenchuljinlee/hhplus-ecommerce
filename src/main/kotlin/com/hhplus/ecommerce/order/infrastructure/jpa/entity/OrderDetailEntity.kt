package com.hhplus.ecommerce.order.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_detail")
class OrderDetailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "product_id")
    var productId: Long,
    @Column(name = "quantity")
    var quantity: Int,
    @Column(name = "price")
    var price: Long,

    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: OrderEntity,
)