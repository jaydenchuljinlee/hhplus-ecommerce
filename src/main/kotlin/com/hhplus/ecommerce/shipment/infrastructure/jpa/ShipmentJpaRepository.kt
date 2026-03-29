package com.hhplus.ecommerce.shipment.infrastructure.jpa

import com.hhplus.ecommerce.shipment.common.ShipmentStatus
import com.hhplus.ecommerce.shipment.infrastructure.jpa.entity.ShipmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ShipmentJpaRepository : JpaRepository<ShipmentEntity, Long> {
    fun findByOrderId(orderId: Long): ShipmentEntity?

    /** 배송 완료 후 N일이 지난 DELIVERED 상태 조회 — 자동 구매확정용 */
    @Query("""
        SELECT s FROM ShipmentEntity s
        WHERE s.status = 'DELIVERED'
          AND s.deliveredAt < :threshold
    """)
    fun findDeliveredBefore(
        @Param("threshold") threshold: LocalDateTime
    ): List<ShipmentEntity>
}
