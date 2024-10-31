package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class SpinLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(SpinLockSupporter::class.java);

    override fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T) {
        val spinLock = redissonClient.getSpinLock(key)

        if (spinLock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.info("REDIS:LOCK:SPINE:$key")
            try {
                action()
            } finally {
                spinLock.unlock()
            }
        }

    }
}