package com.hhplus.ecommerce.common.anotation.aspect.enums

/**
 * Redis 분산 락 전략
 *
 * 현재 PUB_SUB (Redisson Pub/Sub 기반) 전략만 구현되어 있습니다.
 * 추후 전략별 IRedisLockSupporter 구현체 추가 시 확장 가능합니다.
 */
enum class RedisLockStrategy {
    PUB_SUB
}
