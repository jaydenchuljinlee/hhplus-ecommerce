package com.hhplus.ecommerce.product.domain.repository

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import java.time.LocalDateTime

interface IStockReservationRepository {
    fun save(entity: StockReservationEntity): StockReservationEntity
    fun findByOrderId(orderId: Long): List<StockReservationEntity>
    fun findByOrderIdAndStatus(orderId: Long, status: StockReservationStatus): List<StockReservationEntity>
    fun findExpiredReservations(now: LocalDateTime): List<StockReservationEntity>
    fun saveAll(entities: List<StockReservationEntity>): List<StockReservationEntity>
}
