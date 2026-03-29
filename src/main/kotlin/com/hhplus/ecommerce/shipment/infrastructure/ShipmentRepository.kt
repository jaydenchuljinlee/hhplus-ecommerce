package com.hhplus.ecommerce.shipment.infrastructure

import com.hhplus.ecommerce.shipment.domain.repository.IShipmentRepository
import com.hhplus.ecommerce.shipment.infrastructure.exception.ShipmentNotFoundException
import com.hhplus.ecommerce.shipment.infrastructure.jpa.ShipmentJpaRepository
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ShipmentRepository(
    private val shipmentJpaRepository: ShipmentJpaRepository
) : IShipmentRepository {

    override fun save(entity: ShipmentEntity): ShipmentEntity {
        return shipmentJpaRepository.save(entity)
    }

    override fun findById(shipmentId: Long): ShipmentEntity {
        return shipmentJpaRepository.findById(shipmentId)
            .orElseThrow { ShipmentNotFoundException() }
    }

    override fun findByOrderId(orderId: Long): ShipmentEntity? {
        return shipmentJpaRepository.findByOrderId(orderId)
    }

    override fun findDeliveredBefore(threshold: LocalDateTime): List<ShipmentEntity> {
        return shipmentJpaRepository.findDeliveredBefore(threshold)
    }
}
