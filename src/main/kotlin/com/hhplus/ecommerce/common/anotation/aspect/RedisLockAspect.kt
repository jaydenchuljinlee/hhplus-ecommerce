package com.hhplus.ecommerce.common.anotation.aspect

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.common.anotation.aspect.enums.RedisLockStrategy
import com.hhplus.ecommerce.infrastructure.redis.IRedisLockSupporter
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.client.RedisException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Aspect
@Component
class RedisLockAspect(
    private val pubSubLockSupporter: IRedisLockSupporter,
    private val transactionManager: PlatformTransactionManager
) {
    private val logger = LoggerFactory.getLogger(RedisLockAspect::class.java)
    private val waitTime = 5L
    private val releaseTime = -1L // ✅ Redisson Watchdog 활성화
    private val parser: ExpressionParser = SpelExpressionParser()

    @Around("@annotation(redisLock)")
    fun around(joinPoint: ProceedingJoinPoint, redisLock: RedisLock): Any? {
        val key = parseKey(redisLock.key, joinPoint)
        val strategy = redisLock.strategy

        val lockSupporter = when (strategy) {
            RedisLockStrategy.SIMPLE, RedisLockStrategy.SPIN, RedisLockStrategy.FAIR -> pubSubLockSupporter
            RedisLockStrategy.PUB_SUB -> pubSubLockSupporter
        }

        return try {
            lockSupporter.withLock(key) {
                TransactionTemplate(transactionManager).execute { transactionStatus ->
                    try {
                        joinPoint.proceed()
                    } catch (ex: Throwable) {
                        transactionStatus.setRollbackOnly()
                        logger.error("REDIS:LOCK:ERROR:$key -> 트랜잭션 롤백 발생")
                        throw ex
                    }
                }
            }
        } catch (ex: RedisException) {
            logger.warn("REDIS:LOCK:FALLBACK:$key — Redis 연결 실패, 락 없이 트랜잭션 직접 실행 (데이터 정합성 위험)", ex)
            executeWithoutLock(joinPoint)
        } catch (ex: DataAccessResourceFailureException) {
            logger.warn("REDIS:LOCK:FALLBACK:$key — Redis 데이터 접근 실패, 락 없이 트랜잭션 직접 실행 (데이터 정합성 위험)", ex)
            executeWithoutLock(joinPoint)
        }
    }

    /**
     * Redis 장애 시 Fallback: 분산 락 없이 단순 트랜잭션만 보장하여 실행.
     * 동시성 제어가 불완전하므로 알람/모니터링 연동을 권장합니다.
     */
    private fun executeWithoutLock(joinPoint: ProceedingJoinPoint): Any? {
        return TransactionTemplate(transactionManager).execute { transactionStatus ->
            try {
                joinPoint.proceed()
            } catch (ex: Throwable) {
                transactionStatus.setRollbackOnly()
                throw ex
            }
        }
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
