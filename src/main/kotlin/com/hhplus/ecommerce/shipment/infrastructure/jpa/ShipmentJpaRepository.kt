package com.hhplus.ecommerce.shipment.infrastructure.jpa

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ShipmentJpaRepository : JpaRepository<ShipmentEntity, Long> {
    fun findByOrderId(orderId: Long): ShipmentEntity?
    fun findByStatusAndDeliveredAtBefore(status: ShipmentStatus, deliveredAt: LocalDateTime): List<ShipmentEntity>
}
