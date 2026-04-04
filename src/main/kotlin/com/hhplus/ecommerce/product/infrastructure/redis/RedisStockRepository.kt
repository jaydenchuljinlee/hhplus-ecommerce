package com.hhplus.ecommerce.product.infrastructure.redis

import com.hhplus.ecommerce.product.domain.repository.IRedisStockRepository
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class RedisStockRepository(
    private val redissonClient: RedissonClient
) : IRedisStockRepository {

    private val logger = LoggerFactory.getLogger(RedisStockRepository::class.java)

    companion object {
        private const val AVAILABLE_KEY_PREFIX = "stock:available:"
        private const val RESERVATION_KEY_PREFIX = "stock:reservation:"
    }

    override fun reserve(productDetailId: Long, quantity: Int): Boolean {
        val key = "$AVAILABLE_KEY_PREFIX$productDetailId"
        val result = redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval<Long>(
            org.redisson.api.RScript.Mode.READ_WRITE,
            StockLuaScripts.RESERVE,
            org.redisson.api.RScript.ReturnType.INTEGER,
            listOf(key),
            quantity.toString()
        )
        return result == 1L
    }

    override fun release(productDetailId: Long, quantity: Int) {
        val key = "$AVAILABLE_KEY_PREFIX$productDetailId"
        redissonClient.getScript(org.redisson.client.codec.StringCodec.INSTANCE).eval<Long>(
            org.redisson.api.RScript.Mode.READ_WRITE,
            StockLuaScripts.RELEASE,
            org.redisson.api.RScript.ReturnType.INTEGER,
            listOf(key),
            quantity.toString()
        )
    }

    override fun getAvailableStock(productDetailId: Long): Int {
        val key = "$AVAILABLE_KEY_PREFIX$productDetailId"
        return redissonClient.getAtomicLong(key).get().toInt()
    }

    override fun initializeStock(productDetailId: Long, availableQuantity: Int) {
        val key = "$AVAILABLE_KEY_PREFIX$productDetailId"
        redissonClient.getAtomicLong(key).set(availableQuantity.toLong())
        logger.debug("REDIS:STOCK:INIT - productDetailId={}, availableQuantity={}", productDetailId, availableQuantity)
    }

    override fun saveReservationInfo(orderId: Long, productDetailId: Long, quantity: Int) {
        val key = "$RESERVATION_KEY_PREFIX$orderId"
        redissonClient.getMap<String, String>(key).put(productDetailId.toString(), quantity.toString())
    }

    override fun getReservationInfo(orderId: Long): Map<Long, Int> {
        val key = "$RESERVATION_KEY_PREFIX$orderId"
        return redissonClient.getMap<String, String>(key)
            .readAllMap()
            .mapKeys { it.key.toLong() }
            .mapValues { it.value.toInt() }
    }

    override fun removeReservationInfo(orderId: Long) {
        val key = "$RESERVATION_KEY_PREFIX$orderId"
        redissonClient.getMap<String, String>(key).delete()
    }
}
