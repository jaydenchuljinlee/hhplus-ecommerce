package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class SpinLockSupporter(
    private val redissonClient: RedissonClient
) : IRedisLockSupporter {
    private val waitTime = 10L
    private val releaseTime = 5L

    private val logger = LoggerFactory.getLogger(SpinLockSupporter::class.java)

    override fun <T> withLock(key: String, action: () -> T): T {
        val lock = redissonClient.getSpinLock(key)

        if (!lock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.error("REDIS:SPIN:LOCK:ERROR:$key -> 락을 얻지 못 했습니다.")
            throw IllegalStateException("Could not acquire spin lock for key: $key")
        }

        return try {
            logger.info("REDIS:SPIN:ACQUIRE:INFO:$key")
            action()
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                logger.info("REDIS:SPIN:UNLOCK:INFO:$key")
            }
        }
    }
}
