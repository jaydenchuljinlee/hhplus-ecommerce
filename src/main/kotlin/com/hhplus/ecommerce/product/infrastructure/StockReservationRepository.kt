package com.hhplus.ecommerce.product.infrastructure

import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.StockReservationJpaRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class StockReservationRepository(
    private val stockReservationJpaRepository: StockReservationJpaRepository
) : IStockReservationRepository {

    override fun save(entity: StockReservationEntity): StockReservationEntity {
        return stockReservationJpaRepository.save(entity)
    }

    override fun findByOrderId(orderId: Long): List<StockReservationEntity> {
        return stockReservationJpaRepository.findByOrderId(orderId)
    }

    override fun findByOrderIdAndStatus(orderId: Long, status: StockReservationStatus): List<StockReservationEntity> {
        return stockReservationJpaRepository.findByOrderIdAndStatus(orderId, status)
    }

    override fun findExpiredReservations(now: LocalDateTime): List<StockReservationEntity> {
        return stockReservationJpaRepository.findExpiredReservations(now)
    }

    override fun saveAll(entities: List<StockReservationEntity>): List<StockReservationEntity> {
        return stockReservationJpaRepository.saveAll(entities)
    }
}
