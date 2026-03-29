package com.hhplus.ecommerce.common.anotation.aspect

import com.hhplus.ecommerce.common.anotation.RateLimit
import com.hhplus.ecommerce.common.exception.RateLimitExceededException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RateType
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Aspect
@Component
class RateLimitAspect(
    private val redissonClient: RedissonClient
) {
    private val logger = LoggerFactory.getLogger(RateLimitAspect::class.java)
    private val parser: ExpressionParser = SpelExpressionParser()

    @Around("@annotation(rateLimit)")
    fun around(joinPoint: ProceedingJoinPoint, rateLimit: RateLimit): Any? {
        val keyValue = parseKey(rateLimit.key, joinPoint)
        val redisKey = "rate_limit:${joinPoint.signature.name}:$keyValue"

        val rateLimiter = redissonClient.getRateLimiter(redisKey)
        rateLimiter.trySetRate(RateType.PER_CLIENT, rateLimit.limit, rateLimit.seconds, RateIntervalUnit.SECONDS)

        if (!rateLimiter.tryAcquire()) {
            logger.warn("RATE_LIMIT:EXCEEDED - key=$redisKey, limit=${rateLimit.limit}/${rateLimit.seconds}s")
            throw RateLimitExceededException("요청 한도를 초과했습니다. ${rateLimit.seconds}초당 ${rateLimit.limit}회까지 허용됩니다.")
        }

        return joinPoint.proceed()
    }

    private fun parseKey(keyExpression: String, joinPoint: ProceedingJoinPoint): String {
        val signature = joinPoint.signature as MethodSignature
        val paramNames = signature.parameterNames
        val args = joinPoint.args

        val context = StandardEvaluationContext()
        paramNames.forEachIndexed { index, name -> context.setVariable(name, args[index]) }

        return parser.parseExpression(keyExpression).getValue(context, String::class.java) ?: ""
    }
}
