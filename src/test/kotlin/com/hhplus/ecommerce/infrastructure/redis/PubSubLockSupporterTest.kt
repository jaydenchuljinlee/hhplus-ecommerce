package com.hhplus.ecommerce.infrastructure.redis

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.balance.infrastructure.jpa.BalanceJpaRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class PubSubLockSupporterTest: IntegrationConfig() {
    private val userId = 1L
    private val chargeAmount = 100L

    private val logger = LoggerFactory.getLogger(PubSubLockSupporterTest::class.java);

    @Autowired
    private lateinit var balanceRepository: BalanceJpaRepository
    @Autowired
    private lateinit var pubSubLockSupporter: PubSubLockSupporter

    @DisplayName("Redisson Pub/Sub Lock 테스트")
    @Test
    fun testPubSubLock() {
        val threadCount = 1000  // 동시 충전 요청을 보낼 스레드 수
        val latch = CountDownLatch(threadCount)
        val executorService = Executors.newFixedThreadPool(threadCount)

        val startTime = System.nanoTime() // 테스트 시작 시간

        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    val key = "balance:$userId"

                    val lock = pubSubLockSupporter.withLock(key) {
                        val balanceEntity = balanceRepository.findByUserIdWithLock(userId)
                            .orElseThrow { IllegalArgumentException("User not found") }

                        balanceEntity.charge(chargeAmount)
                        balanceRepository.save(balanceEntity)
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