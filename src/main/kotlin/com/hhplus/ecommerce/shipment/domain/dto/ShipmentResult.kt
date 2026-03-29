package com.hhplus.ecommerce.shipment.domain.dto

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import java.time.LocalDateTime

data class ShipmentResult(
    val id: Long,
    val orderId: Long,
    val carrierId: String?,
    val trackingNumber: String?,
    val status: ShipmentStatus,
    val estimatedAt: LocalDateTime?,
    val deliveredAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: ShipmentEntity) = ShipmentResult(
            id = entity.id,
            orderId = entity.orderId,
            carrierId = entity.carrierId,
            trackingNumber = entity.trackingNumber,
            status = entity.status,
            estimatedAt = entity.estimatedAt,
            deliveredAt = entity.deliveredAt,
            createdAt = entity.createdAt
        )
    }
}
