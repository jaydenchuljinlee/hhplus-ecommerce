package com.hhplus.ecommerce

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.OrderRepository
import com.hhplus.ecommerce.product.domain.repository.IRedisStockRepository
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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
    private lateinit var redisStockRepository: IRedisStockRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    private val productDetailId = 4L

    @BeforeEach
    fun setUp() {
        // SQL 데이터: product_detail.id=4 ('책') quantity=1
        // RedisStockSyncScheduler가 ApplicationReadyEvent로 초기화하지만,
        // 테스트 간 Redis 상태 격리를 위해 명시적으로 재초기화
        redisStockRepository.initializeStock(productDetailId, 1)
    }

    @DisplayName("재고가 하나 남은 상품에 대해 동시 신청을 하게되면, 1명만 성공한다")
    @Test
    fun concurrencyTest() {
        val successUserIds = Collections.synchronizedList(mutableListOf<Long>())
        val errorUserIds = Collections.synchronizedList(mutableListOf<Long>())
        val totalRequests = 5

        val readyLatch = CountDownLatch(1)
        val completeLatch = CountDownLatch(totalRequests)

        val executorService = Executors.newFixedThreadPool(totalRequests)

        for (i in 1..totalRequests) {
            executorService.submit {
                try {
                    readyLatch.await()

                    val detailCommand = OrderDetailCreation(productDetailId, 1, 100)
                    val command = OrderCreation(i.toLong(), listOf(detailCommand))

                    orderFacade.order(command)
                } catch (e: Exception) {
                    println("$i -> ${e.message}")
                } finally {
                    completeLatch.countDown()
                }
            }
        }

        readyLatch.countDown()
        completeLatch.await()
        executorService.shutdown()

        Thread.sleep(500)

        val availableStock = redisStockRepository.getAvailableStock(productDetailId)

        for (i in 1..totalRequests) {
            val orderInfo = orderRepository.findById(i.toLong())
            if (OrderStatus.CANCELED == orderInfo.status) {
                errorUserIds.add(i.toLong())
            } else {
                successUserIds.add(i.toLong())
            }
        }

        assertEquals(0, availableStock) // 재고 1개가 모두 예약되어 가용 재고 = 0
        assertEquals(1, successUserIds.size) // 5건 중 1건만 성공
        assertEquals(4, errorUserIds.size) // 나머지 4건은 실패
    }
}
