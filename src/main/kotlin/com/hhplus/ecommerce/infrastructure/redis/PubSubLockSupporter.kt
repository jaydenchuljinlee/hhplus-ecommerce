package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class PubSubLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val waitTime = 10L
    private val releaseTime = 1L

    private val logger = LoggerFactory.getLogger(PubSubLockSupporter::class.java);

    override fun <T> withLock(key: String, action: () -> T): T {
        val lock = redissonClient.getLock(key)

        if (!lock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.error("REDIS:PUB_SUB:LOCK:ERROR:$key -> 락을 얻지 못 했습니다.")
            throw IllegalStateException("Could not acquire lock for key: $key")
        }

        return try {
            logger.info("REDIS:PUB_SUB:ACQUIRE:INFO:$key")
            action()  // 락 획득 성공 시 `action`의 결과 반환
        } finally {
            logger.info("REDIS:PUB_SUB:UNLOCK:INFO:$key")
            lock.unlock()
        }
    }
}