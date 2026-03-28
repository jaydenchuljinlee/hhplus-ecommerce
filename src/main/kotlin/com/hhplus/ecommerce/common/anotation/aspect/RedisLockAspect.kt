package com.hhplus.ecommerce.common.anotation.aspect

import com.hhplus.ecommerce.common.anotation.RedisLock
import com.hhplus.ecommerce.infrastructure.redis.IRedisLockSupporter
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Aspect
@Component
class RedisLockAspect(
    private val pubSubLockSupporter: IRedisLockSupporter,
    private val transactionManager: PlatformTransactionManager
) {
    private val logger = LoggerFactory.getLogger(RedisLockAspect::class.java)
    private val parser: ExpressionParser = SpelExpressionParser()

    // REQUIRES_NEW: 항상 새 트랜잭션을 시작하여 락 해제 전에 커밋 보장
    private val transactionTemplate = TransactionTemplate(
        transactionManager,
        DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
    )

    @Around("@annotation(redisLock)")
    fun around(joinPoint: ProceedingJoinPoint, redisLock: RedisLock): Any? {
        val key = parseKey(redisLock.key, joinPoint)

        return pubSubLockSupporter.withLock(key) {
            transactionTemplate.execute { transactionStatus ->
                try {
                    joinPoint.proceed()
                } catch (ex: Throwable) {
                    transactionStatus.setRollbackOnly()
                    logger.error("REDIS:LOCK:ERROR:$key -> 트랜잭션 롤백 발생")
                    throw ex
                }
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
