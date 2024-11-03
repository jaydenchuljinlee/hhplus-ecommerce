package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class PubSubLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(PubSubLockSupporter::class.java);

    override fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T) {
        val lock = redissonClient.getLock(key)

        if (lock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.error("REDIS:LOCK:READ_WRITE:$key")
            try {
                action()
            } finally {
                lock.unlock()
            }
        }
    }
}