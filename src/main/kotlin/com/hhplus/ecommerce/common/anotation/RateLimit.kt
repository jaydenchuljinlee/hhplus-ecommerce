package com.hhplus.ecommerce.common.anotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    val key: String,       // SpEL 표현식으로 사용자 식별 키 (e.g. "#request.userId")
    val limit: Long,       // 허용 요청 수
    val seconds: Long      // 시간 윈도우 (초)
)
