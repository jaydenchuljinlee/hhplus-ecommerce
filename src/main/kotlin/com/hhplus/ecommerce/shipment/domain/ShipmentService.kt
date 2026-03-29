package com.hhplus.ecommerce.shipment.domain

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.OrderService
import com.hhplus.ecommerce.shipment.domain.dto.ShipmentResult
import com.hhplus.ecommerce.shipment.domain.repository.IShipmentRepository
import com.hhplus.ecommerce.shipment.infrastructure.exception.ShipmentNotFoundException
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ShipmentService(
    private val shipmentRepository: IShipmentRepository,
    private val orderService: OrderService
) {
    @Transactional
    fun createShipment(orderId: Long): ShipmentResult {
        val shipment = ShipmentEntity(orderId = orderId)
        val saved = shipmentRepository.save(shipment)
        orderService.updateStatus(orderId, OrderStatus.PREPARING)
        return ShipmentResult.from(saved)
    }

    @Transactional
    fun ship(orderId: Long, carrierId: String, trackingNumber: String, estimatedAt: LocalDateTime? = null): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        shipment.ship(carrierId, trackingNumber, estimatedAt)
        val saved = shipmentRepository.save(shipment)
        orderService.updateStatus(orderId, OrderStatus.SHIPPED)
        return ShipmentResult.from(saved)
    }

    @Transactional
    fun deliver(orderId: Long): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        shipment.deliver()
        val saved = shipmentRepository.save(shipment)
        orderService.updateStatus(orderId, OrderStatus.DELIVERED)
        return ShipmentResult.from(saved)
    }

    @Transactional
    fun requestReturn(orderId: Long): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        shipment.requestReturn()
        val saved = shipmentRepository.save(shipment)
        orderService.updateStatus(orderId, OrderStatus.RETURN_REQUESTED)
        return ShipmentResult.from(saved)
    }

    @Transactional(readOnly = true)
    fun getShipment(orderId: Long): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        return ShipmentResult.from(shipment)
    }

    @Transactional
    fun confirmPurchaseForDeliveredBefore(threshold: LocalDateTime): Int {
        val deliveredShipments = shipmentRepository.findDeliveredBefore(threshold)
        deliveredShipments.forEach {
            orderService.updateStatus(it.orderId, OrderStatus.PURCHASE_CONFIRMED)
        }
        return deliveredShipments.size
    }

    private fun getShipmentByOrderId(orderId: Long): ShipmentEntity =
        shipmentRepository.findByOrderId(orderId)
            ?: throw ShipmentNotFoundException("주문 ID에 해당하는 배송 정보가 없습니다. orderId=$orderId")
}
