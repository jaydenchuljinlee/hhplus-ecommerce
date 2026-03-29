package com.hhplus.ecommerce.product.infrastructure.jpa

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface StockReservationJpaRepository : JpaRepository<StockReservationEntity, Long> {
    fun findByOrderId(orderId: Long): List<StockReservationEntity>
    fun findByOrderIdAndStatus(orderId: Long, status: StockReservationStatus): List<StockReservationEntity>

    @Query("""
        SELECT r FROM StockReservationEntity r
        WHERE r.status = 'RESERVED'
          AND r.expiredAt < :now
    """)
    fun findExpiredReservations(@Param("now") now: LocalDateTime): List<StockReservationEntity>
}
