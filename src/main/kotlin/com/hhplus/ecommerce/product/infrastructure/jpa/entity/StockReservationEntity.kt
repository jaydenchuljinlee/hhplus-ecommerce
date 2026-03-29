package com.hhplus.ecommerce.product.infrastructure.jpa.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "stock_reservation")
class StockReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "order_id")
    var orderId: Long,

    @Column(name = "product_detail_id")
    var productDetailId: Long,

    @Column(name = "quantity")
    var quantity: Int,

    @Enumerated(EnumType.STRING)
    var status: StockReservationStatus = StockReservationStatus.RESERVED,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
)
