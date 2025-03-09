package com.hhplus.ecommerce

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.order.infrastructure.OrderRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.ProductDetailJpaRepository
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.order.usecase.dto.OrderDetailCreation
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

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @DisplayName("재고가 하나 남은 상품에 대해 동시 신청을 하게되면, 1명만 성공한다")
    @Test
    fun concurrencyTest() {
        val successUserIds = Collections.synchronizedList(mutableListOf<Long>())
        val errorUserIds = Collections.synchronizedList(mutableListOf<Long>())
        val totalRequests = 5

        // 동시 요청을 대기할 Latch 설정
        val readyLatch = CountDownLatch(1)
        val completeLatch = CountDownLatch(totalRequests)

        // 스레드 풀 생성
        val executorService = Executors.newFixedThreadPool(totalRequests)

        // 5개의 요청을 비동기로 생성하여 실행
        for (i in 1..totalRequests) {
            executorService.submit {
                try {
                    // 모든 스레드가 준비될 때까지 대기
                    readyLatch.await()

                    val detailCommand = OrderDetailCreation(3L, 1, 100)

                    // 요청마다 같은 상품 ID를 사용하여 LectureCommandData 생성
                    val command = OrderCreation(i.toLong(), listOf(detailCommand))

                    // 주문 신청
                    orderFacade.order(command)
                } catch (e: Exception) {
                    println("$i -> ${e.message}")
                } finally {
                    // 요청 완료 시 Latch 카운트 감소
                    completeLatch.countDown()
                }
            }
        }

        // 모든 스레드가 시작되기를 준비한 후 동시에 시작
        readyLatch.countDown() // 모든 스레드가 동시에 시작하도록 Latch 해제

        // 모든 요청이 완료될 때까지 대기
        completeLatch.await()

        // 스레드 풀 종료
        executorService.shutdown()

        Thread.sleep(500)

        val productDetail = productRepository.findById(3).get()

        for (i in 1..totalRequests) {
            val orderInfo = orderRepository.findById(i.toLong())
            if (StateYn.Y == orderInfo.delYn) {
                errorUserIds.add(i.toLong())
            } else {
                successUserIds.add(i.toLong())
            }
        }

        assertEquals(productDetail.quantity, 0) // 수량이 남았는지 검사
        assertEquals(successUserIds.size, 1) // 5건의 요청중 가장 먼저 처리된건이 성공한다.
        assertEquals(errorUserIds.size, 4) // 나머지 요청은 실패한다.
    }


}