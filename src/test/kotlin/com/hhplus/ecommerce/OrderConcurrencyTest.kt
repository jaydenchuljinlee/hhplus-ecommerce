package com.hhplus.ecommerce

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
import com.hhplus.ecommerce.product.infrastructure.jpa.ProductDetailJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class OrderConcurrencyTest: IntegrationConfig() {
    @Autowired
    private lateinit var orderFacade: OrderFacade

    @Autowired
    private lateinit var productRepository: ProductDetailJpaRepository

    @DisplayName("재고가 하나 남은 상품에 대해 동시 신청을 하게되면, 1명만 성공한다")
    @Test
    fun concurrencyTest() {
        val successCount = Collections.synchronizedList(mutableListOf<Long>())
        val failCount = Collections.synchronizedList(mutableListOf<Long>())
        val totalRequests = 5

        val readyLatch = CountDownLatch(1)
        val completeLatch = CountDownLatch(totalRequests)

        val executorService = Executors.newFixedThreadPool(totalRequests)

        for (i in 1..totalRequests) {
            executorService.submit {
                try {
                    readyLatch.await()

                    val detailCommand = OrderDetailCreation(4L, 1, 100)
                    val command = OrderCreation(i.toLong(), listOf(detailCommand))

                    orderFacade.order(command)
                    successCount.add(i.toLong())
                } catch (e: Exception) {
                    println("$i -> ${e.message}")
                    failCount.add(i.toLong())
                } finally {
                    completeLatch.countDown()
                }
            }
        }

        readyLatch.countDown()
        completeLatch.await()
        executorService.shutdown()

        val productDetail = productRepository.findById(4).get()

        assertEquals(1, productDetail.reservedQuantity) // 예약 재고 수량이 1개인지 검사
        assertEquals(1, successCount.size) // 5건의 요청중 가장 먼저 처리된건이 성공한다.
        assertEquals(4, failCount.size) // 나머지 요청은 실패한다.
    }
}
