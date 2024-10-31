package com.hhplus.ecommerce.infrastructure.redis

import com.hhplus.ecommerce.infrastructure.balance.BalanceRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.BalanceJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class SpinLockSupporterTest {
    private val userId = 1L
    private val initialBalance = 1000L
    private val chargeAmount = 100L

    private val logger = LoggerFactory.getLogger(SpinLockSupporterTest::class.java);

    @Autowired
    private lateinit var balanceRepository: BalanceJpaRepository
    @Autowired
    private lateinit var spinLockSupporter: SpinLockSupporter

    @DisplayName("스핀락에서의 동시성 테스트")
    @Test
    fun testSpinLock() {
        val threadCount = 1000  // 동시 충전 요청을 보낼 스레드 수
        val latch = CountDownLatch(threadCount)
        val executorService = Executors.newFixedThreadPool(threadCount)

        val startTime = System.nanoTime() // 테스트 시작 시간

        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    spinLockSupporter.withLock("balance:$userId", 0, 5) {
                        val balanceEntity = balanceRepository.findByUserIdWithLock(userId)
                            .orElseThrow { IllegalArgumentException("User not found") }

                        balanceEntity.charge(chargeAmount)
                        balanceRepository.save(balanceEntity)
                        // println("${balanceEntity.balance}")
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()

        val endTime = System.nanoTime() // 테스트 종료 시간
        val totalTime = (endTime - startTime) / 1_000_000  // 나노초에서 밀리초로 변환

        logger.info("Total test execution time: ${totalTime} ms")

        // 최종 잔액 검증 (initialBalance + chargeAmount * threadCount)
        val finalBalance = balanceRepository.findByUserId(userId).get().balance
        // assertEquals(initialBalance + chargeAmount * threadCount, finalBalance)
    }
}