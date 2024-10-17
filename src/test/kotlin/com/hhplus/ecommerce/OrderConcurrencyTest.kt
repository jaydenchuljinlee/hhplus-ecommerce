package com.hhplus.ecommerce

import com.hhplus.ecommerce.infrastructure.product.jpa.ProductDetailJpaRepository
import com.hhplus.ecommerce.usercase.order.OrderFacade
import com.hhplus.ecommerce.usercase.order.dto.OrderCreation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class OrderConcurrencyTest {
    @Autowired
    private lateinit var orderFacade: OrderFacade

    @Autowired
    private lateinit var productRepository: ProductDetailJpaRepository

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

                    // 요청마다 같은 상품 ID를 사용하여 LectureCommandData 생성
                    val command = OrderCreation(i.toLong(), 1L, 1, 100)

                    // 주문 신청
                    orderFacade.order(command)

                    successUserIds.add(i.toLong())
                } catch (e: Exception) {
                    errorUserIds.add(i.toLong())
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

        val productDetail = productRepository.findById(1).get()

        assertEquals(productDetail.quantity, 0) // 수량이 남았는지 검사
        assertEquals(successUserIds.size, 1) // 첫 번째 요청은 성공한다
        assertEquals(errorUserIds.size, 4) // 두 번째 요청은 실패한다
    }


}