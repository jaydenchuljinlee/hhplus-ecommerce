package com.hhplus.ecommerce

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.balance.infrastructure.BalanceRepository
import com.hhplus.ecommerce.order.usecase.OrderFacade
import com.hhplus.ecommerce.order.usecase.dto.OrderCreation
import com.hhplus.ecommerce.payment.usecase.PaymentFacade
import com.hhplus.ecommerce.payment.usecase.dto.PaymentCreation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class PaymentConcurrencyTest: IntegrationConfig() {

    @Autowired
    private lateinit var paymentFacade: PaymentFacade

    @Autowired
    private lateinit var orderFacade: OrderFacade

    @Autowired
    private lateinit var balanceRepository: BalanceRepository

    @DisplayName("같은 사용자가 동시에 주문을 하면 잔액 정합성이 일치해야 한다")
    @Test
    fun concurrencyDuplicationTest() {
        val totalRequests = 3

        val original = balanceRepository.findByUserId(1)

        // 동시 요청을 대기할 Latch 설정
        val readyLatch = CountDownLatch(1)
        val completeLatch = CountDownLatch(3)

        // 스레드 풀 생성
        val executorService = Executors.newFixedThreadPool(totalRequests)

        // 5개의 요청을 비동기로 생성하여 실행
        for (i in 1..totalRequests) {
            executorService.submit {
                try {
                    // 모든 스레드가 준비될 때까지 대기
                    readyLatch.await()

                    val orderCommand = OrderCreation(1, i.toLong()+1, 1, 100)

                    // 주문 신청
                    val order = orderFacade.order(orderCommand)

                    val payCommand = PaymentCreation(
                        orderId = order.orderId,
                        userId = 1
                    )

                    // 결제
                    paymentFacade.pay(payCommand)

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

        val newOne = balanceRepository.findByUserId(1)

        assertEquals(original.balance - newOne.balance, 300)

    }
}