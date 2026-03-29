package com.hhplus.ecommerce.product.infrastructure

import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.StockReservationJpaRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class StockReservationRepository(
    private val jpaRepository: StockReservationJpaRepository
) : IStockReservationRepository {

    @Transactional
    override fun save(entity: StockReservationEntity?): StockReservationEntity {
        requireNotNull(entity) { "StockReservationEntity must not be null" }
        return jpaRepository.save(entity)
    }

    @Transactional
    override fun saveAll(entities: List<StockReservationEntity>): List<StockReservationEntity> {
        return jpaRepository.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun findByOrderId(orderId: Long): List<StockReservationEntity> {
        return jpaRepository.findByOrderId(orderId)
    }

    @Transactional(readOnly = true)
    override fun findAllByOrderIdAndStatus(orderId: Long, status: StockReservationStatus): List<StockReservationEntity> {
        return jpaRepository.findAllByOrderIdAndStatus(orderId, status)
    }

    @Transactional(readOnly = true)
    override fun findAllByStatusAndExpiredAtBefore(status: StockReservationStatus?, now: LocalDateTime?): List<StockReservationEntity> {
        requireNotNull(status) { "status must not be null" }
        requireNotNull(now) { "now must not be null" }
        return jpaRepository.findAllByStatusAndExpiredAtBefore(status, now)
    }
}
