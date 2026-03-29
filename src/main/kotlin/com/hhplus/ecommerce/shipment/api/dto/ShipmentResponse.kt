package com.hhplus.ecommerce.shipment.api.dto

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import com.hhplus.ecommerce.shipment.domain.dto.ShipmentResult
import java.time.LocalDateTime

data class ShipmentResponse(
    val id: Long,
    val orderId: Long,
    val carrierId: String?,
    val trackingNumber: String?,
    val status: ShipmentStatus,
    val estimatedAt: LocalDateTime?,
    val shippedAt: LocalDateTime?,
    val deliveredAt: LocalDateTime?
) {
    companion object {
        fun from(result: ShipmentResult) = ShipmentResponse(
            id = result.id,
            orderId = result.orderId,
            carrierId = result.carrierId,
            trackingNumber = result.trackingNumber,
            status = result.status,
            estimatedAt = result.estimatedAt,
            shippedAt = result.shippedAt,
            deliveredAt = result.deliveredAt
        )
    }
}
