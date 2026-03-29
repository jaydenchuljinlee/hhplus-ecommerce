package com.hhplus.ecommerce.shipment.domain.repository

import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import java.time.LocalDateTime

interface IShipmentRepository {
    fun save(entity: ShipmentEntity): ShipmentEntity
    fun findById(shipmentId: Long): ShipmentEntity
    fun findByOrderId(orderId: Long): ShipmentEntity?
    fun findDeliveredBefore(threshold: LocalDateTime): List<ShipmentEntity>
}
