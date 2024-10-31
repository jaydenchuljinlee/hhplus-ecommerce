package com.hhplus.ecommerce.infrastructure.redis

import com.hhplus.ecommerce.common.config.RedisTestContainerConfig
import com.hhplus.ecommerce.infrastructure.balance.BalanceRepository
import com.hhplus.ecommerce.infrastructure.balance.jpa.BalanceJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@Import(RedisTestContainerConfig::class)
@SpringBootTest
class FairLockSupporterTest {
    private val userId = 1L
    private val initialBalance = 1000L
    private val chargeAmount = 100L

    private val logger = LoggerFactory.getLogger(FairLockSupporterTest::class.java);

    @Autowired
    private lateinit var balanceRepository: BalanceJpaRepository
    @Autowired
    private lateinit var fairLockSupporter: FairLockSupporter

    @Test
    @Transactional
    fun `test concurrent charge with spin lock`() {
        val threadCount = 1000  // 동시 충전 요청을 보낼 스레드 수
        val latch = CountDownLatch(threadCount)
        val executorService = Executors.newFixedThreadPool(threadCount)

        val startTime = System.nanoTime() // 테스트 시작 시간

        for (i in 1..threadCount) {
            executorService.submit {
                try {
                    fairLockSupporter.withLock("balance:$userId", 0, 5) {
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