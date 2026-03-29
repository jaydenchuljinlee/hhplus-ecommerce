package com.hhplus.ecommerce.shipment.domain.repository

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import java.time.LocalDateTime

interface IShipmentRepository {
    fun findById(id: Long): ShipmentEntity
    fun findByOrderId(orderId: Long): ShipmentEntity?
    fun findDeliveredBefore(dateTime: LocalDateTime): List<ShipmentEntity>
    fun save(entity: ShipmentEntity): ShipmentEntity
}
