package com.hhplus.ecommerce.common.anotation.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.anotation.RedisCacheable
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.time.Duration

@Aspect
@Component
class RedisCacheableAspect(
    private val redissonClient: RedissonClient,
    private val objectMapper: ObjectMapper
) {
    private val parser: ExpressionParser = SpelExpressionParser()

    @Around("@annotation(redisCacheable) && args(dto)")
    fun <T> cacheData(joinPoint: ProceedingJoinPoint, dto: Any, redisCacheable: RedisCacheable): T? {
        val methodSignature = joinPoint.signature as MethodSignature
        val returnType = methodSignature.returnType as Class<T> // 실제 반환 타입 가져오기

        // SpEL을 이용해 동적 키 생성
        val key = if (redisCacheable.key.isNotEmpty()) {
            val context = StandardEvaluationContext().apply {
                setVariable("dto", dto)
            }
            parser.parseExpression(redisCacheable.key).getValue(context, String::class.java)
        } else {
            "${methodSignature.declaringTypeName}:${dto.hashCode()}"
        }

        val codec = TypedJsonJacksonCodec(returnType, objectMapper)
        val bucket = redissonClient.getBucket<T>(key, codec)

        val ttl = redisCacheable.ttl
        val timeUnit = redisCacheable.timeUnit
        val duration = Duration.ofMillis(timeUnit.toMillis(ttl))

        // 캐시에서 데이터 가져오기
        val cachedData = bucket.get()
        if (cachedData != null) {
            // TTL 초기화
            bucket.expire(duration)
            return cachedData
        }

        // 캐시가 없는 경우 실제 메서드를 호출
        val result = joinPoint.proceed() as T

        // 결과를 객체로 직접 캐시에 저장하고 TTL 설정
        bucket.set(result, ttl, timeUnit)
        return result
    }
}