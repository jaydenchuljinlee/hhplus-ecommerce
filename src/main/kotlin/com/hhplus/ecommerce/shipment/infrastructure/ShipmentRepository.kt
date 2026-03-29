package com.hhplus.ecommerce.shipment.infrastructure

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import com.hhplus.ecommerce.shipment.domain.repository.IShipmentRepository
import com.hhplus.ecommerce.shipment.infrastructure.exception.ShipmentNotFoundException
import com.hhplus.ecommerce.shipment.infrastructure.jpa.ShipmentJpaRepository
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ShipmentRepository(
    private val jpaRepository: ShipmentJpaRepository
) : IShipmentRepository {

    override fun findById(id: Long): ShipmentEntity =
        jpaRepository.findById(id).orElseThrow { ShipmentNotFoundException("배송 정보를 찾을 수 없습니다. id=$id") }

    override fun findByOrderId(orderId: Long): ShipmentEntity? =
        jpaRepository.findByOrderId(orderId)

    override fun findDeliveredBefore(dateTime: LocalDateTime): List<ShipmentEntity> =
        jpaRepository.findByStatusAndDeliveredAtBefore(ShipmentStatus.DELIVERED, dateTime)

    override fun save(entity: ShipmentEntity): ShipmentEntity =
        jpaRepository.save(entity)
}
