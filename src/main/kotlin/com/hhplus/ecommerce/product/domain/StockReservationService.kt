package com.hhplus.ecommerce.product.domain

import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import com.hhplus.ecommerce.product.domain.repository.IRedisStockRepository
import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class StockReservationService(
    private val stockReservationRepository: IStockReservationRepository,
    private val productDetailRepository: IProductDetailRepository,
    private val redisStockRepository: IRedisStockRepository
) {
    private val logger = LoggerFactory.getLogger(StockReservationService::class.java)

    @Transactional
    fun reserve(orderId: Long, productDetailId: Long, quantity: Int): StockReservationEntity {
        val reserved = redisStockRepository.reserve(productDetailId, quantity)
        if (!reserved) {
            throw OutOfStockException()
        }

        try {
            redisStockRepository.saveReservationInfo(orderId, productDetailId, quantity)

            val reservation = StockReservationEntity(
                orderId = orderId,
                productDetailId = productDetailId,
                quantity = quantity,
                status = StockReservationStatus.RESERVED,
                expiredAt = LocalDateTime.now().plusMinutes(30)
            )
            return stockReservationRepository.save(reservation)
        } catch (e: Exception) {
            // MySQL 이력 저장 실패 시 Redis 차감 보상
            logger.error("STOCK:RESERVE:ROLLBACK - orderId={}, productDetailId={}", orderId, productDetailId, e)
            redisStockRepository.release(productDetailId, quantity)
            redisStockRepository.removeReservationInfo(orderId)
            throw e
        }
    }

    @Transactional
    fun reserveWithExpiry(orderId: Long, productDetailId: Long, quantity: Int, expiredAt: LocalDateTime): StockReservationEntity {
        val reserved = redisStockRepository.reserve(productDetailId, quantity)
        if (!reserved) {
            throw OutOfStockException()
        }

        try {
            redisStockRepository.saveReservationInfo(orderId, productDetailId, quantity)

            val reservation = StockReservationEntity(
                orderId = orderId,
                productDetailId = productDetailId,
                quantity = quantity,
                status = StockReservationStatus.RESERVED,
                expiredAt = expiredAt
            )
            return stockReservationRepository.save(reservation)
        } catch (e: Exception) {
            logger.error("STOCK:RESERVE_WITH_EXPIRY:ROLLBACK - orderId={}, productDetailId={}", orderId, productDetailId, e)
            redisStockRepository.release(productDetailId, quantity)
            redisStockRepository.removeReservationInfo(orderId)
            throw e
        }
    }

    @Transactional
    fun commit(orderId: Long) {
        val reservations = stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED)
        check(reservations.isNotEmpty()) { "orderId=$orderId 에 대한 RESERVED 예약을 찾을 수 없습니다." }

        reservations.forEach { reservation ->
            val productDetail = productDetailRepository.findByIdForUpdate(reservation.productDetailId)
            productDetail.decreaseQuantity(reservation.quantity)
            productDetailRepository.save(productDetail)
            reservation.status = StockReservationStatus.COMMITTED
        }
        stockReservationRepository.saveAll(reservations)
        redisStockRepository.removeReservationInfo(orderId)
    }

    @Transactional
    fun release(orderId: Long) {
        val reservationInfo = redisStockRepository.getReservationInfo(orderId)

        reservationInfo.forEach { (productDetailId, quantity) ->
            redisStockRepository.release(productDetailId, quantity)
        }
        redisStockRepository.removeReservationInfo(orderId)

        val reservations = stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED)
        reservations.forEach { it.status = StockReservationStatus.RELEASED }
        stockReservationRepository.saveAll(reservations)
    }

    @Transactional
    fun expireOverdue(): Int {
        val now = LocalDateTime.now()
        val expiredReservations = stockReservationRepository.findAllByStatusAndExpiredAtBefore(StockReservationStatus.RESERVED, now)

        expiredReservations.forEach { reservation ->
            redisStockRepository.release(reservation.productDetailId, reservation.quantity)
            reservation.status = StockReservationStatus.EXPIRED
        }
        stockReservationRepository.saveAll(expiredReservations)

        return expiredReservations.size
    }
}
