package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.product.domain.repository.IStockReservationRepository
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
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

    private lateinit var stockReservationService: StockReservationService

    @BeforeEach
    fun before() {
        stockReservationService = StockReservationService(stockReservationRepository, productDetailRepository)
    }

    @DisplayName("reserve 호출 시 StockReservationEntity가 RESERVED 상태로 생성되고 ProductDetail의 reservedQuantity가 증가한다.")
    @Test
    fun reserveCreatesReservedEntityAndIncreasesReservedQuantity() {
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

        given(productDetailRepository.findById(productDetailId)).willReturn(productDetailEntity)

        val savedReservation = StockReservationEntity(
            id = 1L,
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(10)
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

    @DisplayName("commit 호출 시 해당 orderId의 예약이 COMMITTED 상태로 전환되고 실재고가 차감된다.")
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
        productDetailEntity.reserve(quantity)

        val reservation = StockReservationEntity(
            id = 1L,
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(10)
        )

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED))
            .willReturn(listOf(reservation))
        given(productDetailRepository.findById(productDetailId)).willReturn(productDetailEntity)

        // When
        stockReservationService.commit(orderId)

        // Then
        assertEquals(StockReservationStatus.COMMITTED, reservation.status)
        assertEquals(0, productDetailEntity.reservedQuantity)
        assertEquals(7, productDetailEntity.quantity)
    }

    @DisplayName("release 호출 시 해당 orderId의 예약이 RELEASED 상태로 전환되고 예약재고가 복원된다.")
    @Test
    fun releaseTransitionsReservationToReleasedAndRestoresReservedStock() {
        // Given
        val orderId = 1L
        val productDetailId = 10L
        val quantity = 4

        val productDetailEntity = ProductDetailEntity(
            id = productDetailId,
            productId = 1L,
            productOptionId = 1L,
            quantity = 10
        )
        productDetailEntity.reserve(quantity)

        val reservation = StockReservationEntity(
            id = 1L,
            orderId = orderId,
            productDetailId = productDetailId,
            quantity = quantity,
            status = StockReservationStatus.RESERVED,
            expiredAt = LocalDateTime.now().plusMinutes(10)
        )

        given(stockReservationRepository.findAllByOrderIdAndStatus(orderId, StockReservationStatus.RESERVED))
            .willReturn(listOf(reservation))
        given(productDetailRepository.findById(productDetailId)).willReturn(productDetailEntity)

        // When
        stockReservationService.release(orderId)

        // Then
        assertEquals(StockReservationStatus.RELEASED, reservation.status)
        assertEquals(0, productDetailEntity.reservedQuantity)
        assertEquals(10, productDetailEntity.availableQuantity)
    }

    @DisplayName("expireOverdue 호출 시 만료된 예약이 EXPIRED 처리되고 예약재고가 반납된다.")
    @Test
    fun expireOverdueProcessesExpiredReservationsAndReturnsReservedStock() {
        // Given
        val productDetailId = 10L
        val quantity = 2

        val productDetailEntity = ProductDetailEntity(
            id = productDetailId,
            productId = 1L,
            productOptionId = 1L,
            quantity = 10
        )
        productDetailEntity.reserve(quantity)

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
        given(productDetailRepository.findById(productDetailId)).willReturn(productDetailEntity)

        // When
        val expiredCount = stockReservationService.expireOverdue()

        // Then
        assertEquals(1, expiredCount)
        assertEquals(StockReservationStatus.EXPIRED, expiredReservation.status)
        assertEquals(0, productDetailEntity.reservedQuantity)
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
