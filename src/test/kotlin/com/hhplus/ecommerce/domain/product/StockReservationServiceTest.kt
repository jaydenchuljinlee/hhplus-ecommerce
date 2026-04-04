package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import com.hhplus.ecommerce.product.domain.repository.IRedisStockRepository
import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class StockReservationServiceTest {

    @Mock
    private lateinit var stockReservationRepository: IStockReservationRepository

    @Mock
    private lateinit var productDetailRepository: IProductDetailRepository

    @Mock
    private lateinit var redisStockRepository: IRedisStockRepository

    private lateinit var stockReservationService: StockReservationService

    @BeforeEach
    fun before() {
        stockReservationService = StockReservationService(stockReservationRepository, productDetailRepository, redisStockRepository)
    }

    @DisplayName("reserve 호출 시 Redis에서 재고를 차감하고 StockReservationEntity가 RESERVED 상태로 생성된다.")
    @Test
    fun reserveCreatesReservedEntityViaRedis() {
        // Given
        val orderId = 1L
        val productDetailId = 10L
        val quantity = 3

        given(redisStockRepository.reserve(productDetailId, quantity)).willReturn(true)

        val savedReservation = StockReservationEntity(
            id = 1L,
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(30)
        )
        given(stockReservationRepository.save(org.mockito.ArgumentMatchers.any())).willReturn(savedReservation)

        // When
        val result = stockReservationService.reserve(orderId, productDetailId, quantity)

        // Then
        assertEquals(StockReservationStatus.RESERVED, result.status)
        assertEquals(orderId, result.orderId)
        assertEquals(productDetailId, result.productDetailId)
        assertEquals(quantity, result.quantity)
    }

    @DisplayName("reserve 호출 시 Redis 재고가 부족하면 OutOfStockException이 발생한다.")
    @Test
    fun reserveThrowsOutOfStockWhenRedisStockInsufficient() {
        // Given
        val orderId = 1L
        val productDetailId = 10L
        val quantity = 5

        given(redisStockRepository.reserve(productDetailId, quantity)).willReturn(false)

        // When & Then
        assertThrows<OutOfStockException> {
            stockReservationService.reserve(orderId, productDetailId, quantity)
        }
    }

    @DisplayName("commit 호출 시 예약이 COMMITTED 상태로 전환되고 MySQL 실재고가 차감된다.")
    @Test
    fun commitTransitionsReservationToCommittedAndDecreasesActualStock() {
        // Given
        val orderId = 1L
        val productDetailId = 10L
        val quantity = 3

        val productDetailEntity = ProductDetailEntity(
            id = productDetailId,
            productId = 1L,
            productOptionId = 1L,
            quantity = 10
        )

        val reservation = StockReservationEntity(
            id = 1L,
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(30)
        )

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED))
            .willReturn(listOf(reservation))
        given(productDetailRepository.findByIdForUpdate(productDetailId)).willReturn(productDetailEntity)

        // When
        stockReservationService.commit(orderId)

        // Then
        assertEquals(StockReservationStatus.COMMITTED, reservation.status)
        assertEquals(7, productDetailEntity.quantity)
    }

    @DisplayName("release 호출 시 Redis 재고가 복원되고 예약이 RELEASED 상태로 전환된다.")
    @Test
    fun releaseRestoresRedisStockAndTransitionsToReleased() {
        // Given
        val orderId = 1L
        val productDetailId = 10L
        val quantity = 4

        val reservation = StockReservationEntity(
            id = 1L,
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(30)
        )

        given(redisStockRepository.getReservationInfo(orderId)).willReturn(mapOf(productDetailId to quantity))
        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED))
            .willReturn(listOf(reservation))

        // When
        stockReservationService.release(orderId)

        // Then
        assertEquals(StockReservationStatus.RELEASED, reservation.status)
    }

    @DisplayName("expireOverdue 호출 시 만료된 예약이 EXPIRED 처리되고 Redis 재고가 복원된다.")
    @Test
    fun expireOverdueProcessesExpiredReservationsAndRestoresRedisStock() {
        // Given
        val productDetailId = 10L
        val quantity = 2

        val expiredReservation = StockReservationEntity(
            id = 1L,
            orderId = 99L,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().minusMinutes(1)
        )

        given(stockReservationRepository.findAllByStatusAndExpiredAtBefore(
            org.mockito.ArgumentMatchers.eq(StockReservationStatus.RESERVED),
            org.mockito.ArgumentMatchers.any()
        )).willReturn(listOf(expiredReservation))

        // When
        val expiredCount = stockReservationService.expireOverdue()

        // Then
        assertEquals(1, expiredCount)
        assertEquals(StockReservationStatus.EXPIRED, expiredReservation.status)
    }

    @DisplayName("이미 COMMITTED 상태의 예약에 commit을 시도하면 예외가 발생한다.")
    @Test
    fun commitThrowsExceptionWhenReservationAlreadyCommitted() {
        // Given
        val orderId = 1L

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED))
            .willReturn(emptyList())

        // When & Then
        assertThrows<IllegalStateException> {
            stockReservationService.commit(orderId)
        }
    }
}
