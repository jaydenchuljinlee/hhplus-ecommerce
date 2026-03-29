package com.hhplus.ecommerce.shipment.infrastructure.jpa.entity

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "shipment")
class ShipmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "order_id", nullable = false)
    var orderId: Long,

    @Column(name = "carrier_id")
    var carrierId: String? = null,

    @Column(name = "tracking_number")
    var trackingNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ShipmentStatus = ShipmentStatus.PREPARING,

    @Column(name = "estimated_at")
    var estimatedAt: LocalDateTime? = null,

    @Column(name = "shipped_at")
    var shippedAt: LocalDateTime? = null,

    @Column(name = "delivered_at")
    var deliveredAt: LocalDateTime? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun ship(carrierId: String, trackingNumber: String, estimatedAt: LocalDateTime? = null) {
        this.carrierId = carrierId
        this.trackingNumber = trackingNumber
        this.estimatedAt = estimatedAt
        this.status = ShipmentStatus.SHIPPED
        this.shippedAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    fun deliver() {
        this.status = ShipmentStatus.DELIVERED
        this.deliveredAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    fun requestReturn() {
        require(status == ShipmentStatus.DELIVERED) { "배송 완료 상태에서만 반품 요청이 가능합니다." }
        this.status = ShipmentStatus.RETURN_REQUESTED
        this.updatedAt = LocalDateTime.now()
    }

    fun completeReturn() {
        require(status == ShipmentStatus.RETURN_REQUESTED) { "반품 요청 상태에서만 반품 완료 처리가 가능합니다." }
        this.status = ShipmentStatus.RETURNED
        this.updatedAt = LocalDateTime.now()
    }
}
