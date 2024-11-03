package com.hhplus.ecommerce.common.anotation.aspect

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.common.anotation.aspect.enums.RedisLockStrategy
import com.hhplus.ecommerce.infrastructure.redis.IRedisLockSupporter
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Aspect
@Component
class RedisLockAspect(
    private val redissonClient: RedissonClient,
    private val pubSubLockSupporter: IRedisLockSupporter
) {
    private val logger = LoggerFactory.getLogger(RedisLockAspect::class.java)
    private val waitTime = 10L
    private val releaseTime = 1L
    private val parser: ExpressionParser = SpelExpressionParser()

    @Around("@annotation(redisLock)")
    fun around(joinPoint: ProceedingJoinPoint, redisLock: RedisLock): Any? {
        val key = parseKey(redisLock.key, joinPoint)
        val strategy = redisLock.strategy

        val lockSupporter = when (strategy) {
            RedisLockStrategy.SIMPLE, RedisLockStrategy.SPIN, RedisLockStrategy.FAIR -> pubSubLockSupporter
            RedisLockStrategy.PUB_SUB -> pubSubLockSupporter
        }

        val lock = redissonClient.getLock(key)
        if (!lock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.error("REDIS:PUB_SUB:LOCK:ERROR:$key -> 락을 얻지 못 했습니다.")
            throw IllegalStateException("Could not acquire lock for key: $key")
        }

        return lockSupporter.withLock(key) {
            joinPoint.proceed()
        }
    }

    // SpEL 표현식을 평가하여 실제 락 키로 변환하는 함수
    private fun parseKey(keyExpression: String, joinPoint: ProceedingJoinPoint): String {
        val signature = joinPoint.signature as MethodSignature
        val paramNames = signature.parameterNames
        val args = joinPoint.args

        val context = StandardEvaluationContext()
        paramNames.forEachIndexed { index, name -> context.setVariable(name, args[index]) }

        return parser.parseExpression(keyExpression).getValue(context, String::class.java) ?: ""
    }
}