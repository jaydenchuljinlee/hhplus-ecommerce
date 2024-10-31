package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PubSubLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(PubSubLockSupporter::class.java);
    override fun getLock(key: String): RLock {
        return redissonClient.getLock(key)
    }

}