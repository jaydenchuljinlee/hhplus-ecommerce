package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.product.domain.StockReservationService
import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.StockReservationStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StockReservationIntegrationTest : IntegrationConfig() {

    @Autowired
    private lateinit var stockReservationService: StockReservationService

    @Autowired
    private lateinit var productDetailRepository: IProductDetailRepository

    private lateinit var productDetailEntity: ProductDetailEntity

    @BeforeEach
    fun before() {
        productDetailEntity = productDetailRepository.save(
            ProductDetailEntity(
                productId = 1L,
                productOptionId = 1L,
                quantity = 10
            )
        )
    }

    @DisplayName("동시에 여러 요청이 reserve를 호출해도 재고 한도를 초과하지 않는다.")
    @Test
    fun concurrentReserveDoesNotExceedStockLimit() {
        // Given
        val productDetailId = productDetailEntity.id
        val threadCount = 15
        val reserveAmount = 1
        val executor = Executors.newFixedThreadPool(threadCount)
        val readyLatch = CountDownLatch(threadCount)
        val completeLatch = CountDownLatch(threadCount)
        val successResults = Collections.synchronizedList(mutableListOf<Long>())
        val failResults = Collections.synchronizedList(mutableListOf<Exception>())

        // When
        repeat(threadCount) { index ->
            executor.submit {
                readyLatch.countDown()
                readyLatch.await()
                try {
                    val reservation = stockReservationService.reserve(index.toLong() + 100L, productDetailId, reserveAmount)
                    successResults.add(reservation.id)
                } catch (e: OutOfStockException) {
                    failResults.add(e)
                } finally {
                    completeLatch.countDown()
                }
            }
        }
        completeLatch.await()
        executor.shutdown()

        // Then
        val refreshed = productDetailRepository.findById(productDetailId)
        assertTrue(successResults.size <= 10, "성공한 예약 수(${successResults.size})가 재고(10)를 초과해서는 안 된다.")
        assertEquals(successResults.size, refreshed.reservedQuantity)
    }

    @DisplayName("reserve 후 commit하면 실재고가 감소하고 예약재고가 0으로 돌아온다.")
    @Test
    fun commitAfterReserveDecreasesActualStockAndClearsReservedQuantity() {
        // Given
        val orderId = 200L
        val productDetailId = productDetailEntity.id
        val quantity = 3
        stockReservationService.reserve(orderId, productDetailId, quantity)

        // When
        stockReservationService.commit(orderId)

        // Then
        val refreshed = productDetailRepository.findById(productDetailId)
        assertEquals(0, refreshed.reservedQuantity)
        assertEquals(7, refreshed.quantity)
    }

    @DisplayName("reserve 후 release하면 availableQuantity가 원래대로 복원된다.")
    @Test
    fun releaseAfterReserveRestoresAvailableQuantityToOriginal() {
        // Given
        val orderId = 300L
        val productDetailId = productDetailEntity.id
        val quantity = 4
        stockReservationService.reserve(orderId, productDetailId, quantity)

        // When
        stockReservationService.release(orderId)

        // Then
        val refreshed = productDetailRepository.findById(productDetailId)
        assertEquals(0, refreshed.reservedQuantity)
        assertEquals(10, refreshed.availableQuantity)
    }

    @DisplayName("expireOverdue는 만료 기한이 지난 RESERVED 항목만 처리하고 다른 상태의 항목은 건드리지 않는다.")
    @Test
    fun expireOverdueOnlyProcessesExpiredReservedItemsAndIgnoresOtherStatuses() {
        // Given
        val activeOrderId = 400L
        val expiredOrderId = 401L
        val committedOrderId = 402L
        val productDetailId = productDetailEntity.id

        // 활성 예약 (만료되지 않음)
        stockReservationService.reserve(activeOrderId, productDetailId, 1)

        // 만료된 예약 직접 삽입 (expiredAt을 과거로 설정)
        stockReservationService.reserveWithExpiry(
            orderId = expiredOrderId,
            productDetailId = productDetailId,
            quantity = 2,
            expiredAt = java.time.LocalDateTime.now().minusMinutes(1)
        )

        // 커밋된 예약
        stockReservationService.reserve(committedOrderId, productDetailId, 1)
        stockReservationService.commit(committedOrderId)

        val reservedQuantityBeforeExpire = productDetailRepository.findById(productDetailId).reservedQuantity

        // When
        val expiredCount = stockReservationService.expireOverdue()

        // Then
        val refreshed = productDetailRepository.findById(productDetailId)
        assertEquals(1, expiredCount, "만료 기한이 지난 RESERVED 항목 1개만 처리되어야 한다.")
        assertEquals(reservedQuantityBeforeExpire - 2, refreshed.reservedQuantity)
    }
}
