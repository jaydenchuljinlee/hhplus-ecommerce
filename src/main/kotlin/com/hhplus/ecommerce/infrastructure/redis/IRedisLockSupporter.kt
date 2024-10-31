package com.hhplus.ecommerce.infrastructure.redis

import org.redisson.api.RLock

interface IRedisLockSupporter {
    fun getLock(key: String): RLock
}