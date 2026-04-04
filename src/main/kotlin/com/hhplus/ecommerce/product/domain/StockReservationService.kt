package com.hhplus.ecommerce.product.domain

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class StockReservationService(
    private val stockReservationRepository: IStockReservationRepository,
    private val productDetailRepository: IProductDetailRepository
) {

    @RedisLock(key = "'stock:' + #productDetailId")
    fun reserve(orderId: Long, productDetailId: Long, quantity: Int): StockReservationEntity {
        val productDetail = productDetailRepository.findById(productDetailId)
        productDetail.reserve(quantity)
        productDetailRepository.save(productDetail)

        val reservation = StockReservationEntity(
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(30)
        )
        return stockReservationRepository.save(reservation)
    }

    @Transactional
    fun reserveWithExpiry(orderId: Long, productDetailId: Long, quantity: Int, expiredAt: LocalDateTime): StockReservationEntity {
        val productDetail = productDetailRepository.findById(productDetailId)
        productDetail.reserve(quantity)
        productDetailRepository.save(productDetail)

        val reservation = StockReservationEntity(
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = expiredAt
        )
        return stockReservationRepository.save(reservation)
    }

    @Transactional
    fun commit(orderId: Long) {
        val reservations = stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED)
        check(reservations.isNotEmpty()) { "orderId=$orderId 에 대한 RESERVED 예약을 찾을 수 없습니다." }

        reservations.forEach { reservation ->
            val productDetail = productDetailRepository.findById(reservation.productDetailId)
            productDetail.commit(reservation.quantity)
            productDetailRepository.save(productDetail)
            reservation.status = StockReservationStatus.COMMITTED
        }
        stockReservationRepository.saveAll(reservations)
    }

    @Transactional
    fun release(orderId: Long) {
        val reservations = stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED)

        reservations.forEach { reservation ->
            val productDetail = productDetailRepository.findById(reservation.productDetailId)
            productDetail.release(reservation.quantity)
            productDetailRepository.save(productDetail)
            reservation.status = StockReservationStatus.RELEASED
        }
        stockReservationRepository.saveAll(reservations)
    }

    @Transactional
    fun expireOverdue(): Int {
        val now = LocalDateTime.now()
        val expiredReservations = stockReservationRepository.findAllByStatusAndExpiredAtBefore(StockReservationStatus.RESERVED, now)

        expiredReservations.forEach { reservation ->
            val productDetail = productDetailRepository.findById(reservation.productDetailId)
            productDetail.release(reservation.quantity)
            productDetailRepository.save(productDetail)
            reservation.status = StockReservationStatus.EXPIRED
        }
        stockReservationRepository.saveAll(expiredReservations)

        return expiredReservations.size
    }
}
