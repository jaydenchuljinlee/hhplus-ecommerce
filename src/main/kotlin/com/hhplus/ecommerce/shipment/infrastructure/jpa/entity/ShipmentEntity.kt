package com.hhplus.ecommerce.shipment.infrastructure.jpa.entity

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

/**
 * 배송 엔티티
 *
 * 주문 1건에 배송 1건이 매핑된다.
 */
@Entity
@Table(
    name = "shipment",
    indexes = [
        Index(name = "idx_shipment_order_id", columnList = "order_id"),
        Index(name = "idx_shipment_delivered_at", columnList = "delivered_at")
    ]
)
class ShipmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "order_id", nullable = false, unique = true)
    val orderId: Long,

    /** 택배사 코드 (ex. CJ, LOTTE, HANJIN) */
    @Column(name = "carrier_id", length = 20)
    var carrierId: String? = null,

    /** 운송장 번호 */
    @Column(name = "tracking_number", length = 50)
    var trackingNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ShipmentStatus = ShipmentStatus.PREPARING,

    /** 예상 배송 완료일 */
    @Column(name = "estimated_at")
    var estimatedAt: LocalDateTime? = null,

    /** 실제 배송 완료 시각 */
    @Column(name = "delivered_at")
    var deliveredAt: LocalDateTime? = null,

    @Column(name = "created_at") @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
) {
    /** PREPARING → SHIPPED: 택배사 접수 */
    fun ship(carrierId: String, trackingNumber: String, estimatedAt: LocalDateTime? = null) {
        this.carrierId = carrierId
        this.trackingNumber = trackingNumber
        this.estimatedAt = estimatedAt
        this.status = ShipmentStatus.SHIPPED
        this.updatedAt = LocalDateTime.now()
    }

    /** SHIPPED → IN_TRANSIT: 배송 중 */
    fun startTransit() {
        this.status = ShipmentStatus.IN_TRANSIT
        this.updatedAt = LocalDateTime.now()
    }

    /** IN_TRANSIT → DELIVERED: 배송 완료 */
    fun deliver() {
        this.status = ShipmentStatus.DELIVERED
        this.deliveredAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    /** DELIVERED → RETURN_REQUESTED: 반품 신청 */
    fun requestReturn() {
        this.status = ShipmentStatus.RETURN_REQUESTED
        this.updatedAt = LocalDateTime.now()
    }

    /** RETURN_REQUESTED → RETURNED: 반품 완료 */
    fun completeReturn() {
        this.status = ShipmentStatus.RETURNED
        this.updatedAt = LocalDateTime.now()
    }
}
