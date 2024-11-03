package com.hhplus.ecommerce.common.anotation

import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedisCacheable(
    val key: String = "",
    val ttl: Long = 24,
    val timeUnit: TimeUnit = TimeUnit.HOURS
)