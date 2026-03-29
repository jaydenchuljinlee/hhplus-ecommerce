package com.hhplus.ecommerce.shipment.domain

import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.domain.repository.IOrderRepository
import com.hhplus.ecommerce.shipment.domain.dto.ShipmentResult
import com.hhplus.ecommerce.shipment.domain.repository.IShipmentRepository
import com.hhplus.ecommerce.shipment.infrastructure.exception.ShipmentNotFoundException
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 배송 도메인 서비스
 *
 * 배송 생성, 상태 전이, 조회를 담당한다.
 * 주문 상태와 배송 상태는 함께 동기화된다.
 */
@Service
class ShipmentService(
    private val shipmentRepository: IShipmentRepository,
    private val orderRepository: IOrderRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ShipmentService::class.java)

        /** 구매 자동 확정 기준일 (배송 완료 후 N일) */
        const val AUTO_CONFIRM_DAYS = 7L
    }

    /** 결제 완료 후 배송 레코드 생성 (PREPARING 상태) */
    @Transactional
    fun createShipment(orderId: Long): ShipmentResult {
        val order = orderRepository.findByIdAndStatus(orderId, OrderStatus.CONFIRMED)
        order.startPreparing()
        orderRepository.insertOrUpdate(order)

        val shipment = shipmentRepository.save(ShipmentEntity(orderId = orderId))
        logger.info("SHIPMENT:CREATED orderId={}, shipmentId={}", orderId, shipment.id)
        return ShipmentResult.from(shipment)
    }

    /** 출고 처리 — 택배사 및 운송장 번호 등록 */
    @Transactional
    fun ship(orderId: Long, carrierId: String, trackingNumber: String, estimatedAt: LocalDateTime? = null): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        val order = orderRepository.findById(orderId)

        shipment.ship(carrierId, trackingNumber, estimatedAt)
        order.readyToShip()
        order.ship()

        shipmentRepository.save(shipment)
        orderRepository.insertOrUpdate(order)

        logger.info("SHIPMENT:SHIPPED orderId={}, tracking={}", orderId, trackingNumber)
        return ShipmentResult.from(shipment)
    }

    /** 배송 중 전이 */
    @Transactional
    fun startTransit(orderId: Long): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        val order = orderRepository.findById(orderId)

        shipment.startTransit()
        order.startTransit()

        shipmentRepository.save(shipment)
        orderRepository.insertOrUpdate(order)

        logger.info("SHIPMENT:IN_TRANSIT orderId={}", orderId)
        return ShipmentResult.from(shipment)
    }

    /** 배송 완료 처리 */
    @Transactional
    fun deliver(orderId: Long): ShipmentResult {
        val shipment = getShipmentByOrderId(orderId)
        val order = orderRepository.findById(orderId)

        shipment.deliver()
        order.deliver()

        shipmentRepository.save(shipment)
        orderRepository.insertOrUpdate(order)

        logger.info("SHIPMENT:DELIVERED orderId={}", orderId)
        return ShipmentResult.from(shipment)
    }

    /** 배송 조회 */
    @Transactional(readOnly = true)
    fun getShipmentByOrderId(orderId: Long): ShipmentEntity {
        return shipmentRepository.findByOrderId(orderId)
            ?: throw ShipmentNotFoundException("주문(orderId=$orderId)의 배송 정보를 찾을 수 없습니다.")
    }

    /** 배송 완료 7일 이상 지난 주문 자동 구매 확정 (스케줄러에서 호출) */
    @Transactional
    fun autoConfirmPurchase() {
        val threshold = LocalDateTime.now().minusDays(AUTO_CONFIRM_DAYS)
        val deliveredShipments = shipmentRepository.findDeliveredBefore(threshold)

        if (deliveredShipments.isEmpty()) return

        deliveredShipments.forEach { shipment ->
            try {
                val order = orderRepository.findById(shipment.orderId)
                if (order.status == OrderStatus.DELIVERED) {
                    order.confirmPurchase()
                    orderRepository.insertOrUpdate(order)
                    logger.info("SHIPMENT:AUTO_CONFIRM orderId={}", shipment.orderId)
                }
            } catch (e: Exception) {
                logger.error("SHIPMENT:AUTO_CONFIRM:ERROR orderId={}", shipment.orderId, e)
            }
        }

        logger.info("SHIPMENT:AUTO_CONFIRM:DONE count={}", deliveredShipments.size)
    }
}
