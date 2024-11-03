package com.hhplus.ecommerce.common.anotation

import com.hhplus.ecommerce.common.anotation.aspect.enums.RedisLockStrategy

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedisLock(
    val key: String,
    val strategy: RedisLockStrategy = RedisLockStrategy.PUB_SUB
)
