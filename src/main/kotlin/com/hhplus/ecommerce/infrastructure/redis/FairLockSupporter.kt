package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class FairLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(FairLockSupporter::class.java);

    override fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T) {
        val fairLock: RLock = redissonClient.getFairLock(key)

        if (fairLock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.error("REDIS:LOCK:FAIR:$key")
            try {
                action()
            } finally {
                fairLock.unlock()
            }
        }
    }
}