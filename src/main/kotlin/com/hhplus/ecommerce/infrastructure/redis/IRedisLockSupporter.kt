package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RLock

interface IRedisLockSupporter {
    fun <T> withLock(key: String, action: () -> T): T
}