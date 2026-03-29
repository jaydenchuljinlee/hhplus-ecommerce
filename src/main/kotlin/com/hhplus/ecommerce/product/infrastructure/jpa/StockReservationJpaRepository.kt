package com.hhplus.ecommerce.product.infrastructure.jpa

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface StockReservationJpaRepository : JpaRepository<StockReservationEntity, Long> {
    fun findByOrderId(orderId: Long): List<StockReservationEntity>
    fun findAllByOrderIdAndStatus(orderId: Long, status: StockReservationStatus): List<StockReservationEntity>
    fun findAllByStatusAndExpiredAtBefore(status: StockReservationStatus, now: LocalDateTime): List<StockReservationEntity>
}
