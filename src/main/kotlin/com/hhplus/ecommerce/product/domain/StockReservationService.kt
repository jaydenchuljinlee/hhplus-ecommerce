package com.hhplus.ecommerce.product.domain

import com.hhplus.ecommerce.product.domain.repository.IProductRepository
import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 예약 재고(Soft Reserve) 도메인 서비스
 *
 * reserve()  : 주문 생성 시 가용 재고 임시 점유 → StockReservationEntity RESERVED 생성
 * commit()   : 결제 완료 후 예약 재고 확정 차감 → COMMITTED 전환
 * release()  : 취소/만료 시 예약 해제 → RELEASED/EXPIRED 전환 + ProductDetail.release()
 */
@Service
class StockReservationService(
    private val stockReservationRepository: IStockReservationRepository,
    private val productRepository: IProductRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(StockReservationService::class.java)

        /** 기본 예약 TTL: 10분 */
        private const val RESERVATION_TTL_MINUTES = 10L
    }

    /**
     * 상품 목록에 대해 예약 재고를 점유한다.
     * @param orderId 주문 ID
     * @param items   (productDetailId, quantity) 쌍 목록
     */
    @Transactional
    fun reserve(orderId: Long, items: List<Pair<Long, Int>>) {
        val expiredAt = LocalDateTime.now().plusMinutes(RESERVATION_TTL_MINUTES)

        items.forEach { (productDetailId, quantity) ->
            productRepository.reserveStock(productDetailId, quantity)

            val reservation = StockReservationEntity(
                orderId = orderId,
                productDetailId = productDetailId,
                quantity = quantity,
                expiredAt = expiredAt
            )
            stockReservationRepository.save(reservation)
            logger.debug("STOCK:RESERVE orderId={}, productDetailId={}, qty={}", orderId, productDetailId, quantity)
        }
    }

    /**
     * 결제 완료 후 해당 주문의 예약 재고를 실제 차감으로 확정한다.
     * @param orderId 결제 완료된 주문 ID
     */
    @Transactional
    fun commit(orderId: Long) {
        val reservations = stockReservationRepository.findByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED)

        reservations.forEach { reservation ->
            productRepository.commitStock(reservation.productDetailId, reservation.quantity)
            reservation.commit()
            stockReservationRepository.save(reservation)
            logger.debug("STOCK:COMMIT orderId={}, productDetailId={}, qty={}", orderId, reservation.productDetailId, reservation.quantity)
        }

        logger.info("STOCK:COMMIT:DONE orderId={}, count={}", orderId, reservations.size)
    }

    /**
     * 주문 취소 / 예약 만료 시 임시 점유를 해제한다.
     * @param orderId 취소할 주문 ID
     */
    @Transactional
    fun release(orderId: Long) {
        val reservations = stockReservationRepository.findByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED)

        reservations.forEach { reservation ->
            productRepository.releaseStock(reservation.productDetailId, reservation.quantity)
            reservation.release()
            stockReservationRepository.save(reservation)
            logger.debug("STOCK:RELEASE orderId={}, productDetailId={}, qty={}", orderId, reservation.productDetailId, reservation.quantity)
        }

        logger.info("STOCK:RELEASE:DONE orderId={}, count={}", orderId, reservations.size)
    }

    /**
     * 만료된 예약 재고를 일괄 해제한다. (스케줄러에서 호출)
     */
    @Transactional
    fun releaseExpired() {
        val now = LocalDateTime.now()
        val expiredReservations = stockReservationRepository.findExpiredReservations(now)

        if (expiredReservations.isEmpty()) return

        expiredReservations.forEach { reservation ->
            productRepository.releaseStock(reservation.productDetailId, reservation.quantity)
            reservation.expire()
        }
        stockReservationRepository.saveAll(expiredReservations)

        logger.info("STOCK:EXPIRED:RELEASED count={}", expiredReservations.size)
    }
}
