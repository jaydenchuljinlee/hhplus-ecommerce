package com.hhplus.ecommerce.infrastructure.redis

interface IRedisLockSupporter {
    fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T)
}