package com.hhplus.ecommerce.product.infrastructure.redis

import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import com.hhplus.ecommerce.product.domain.repository.IRedisStockRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RedisStockSyncScheduler(
    private val productDetailRepository: IProductDetailRepository,
    private val redisStockRepository: IRedisStockRepository
) {
    private val logger = LoggerFactory.getLogger(RedisStockSyncScheduler::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun initializeOnStartup() {
        logger.info("REDIS:STOCK:INIT - Redis 재고 초기화 시작")
        syncAllStock()
        logger.info("REDIS:STOCK:INIT - Redis 재고 초기화 완료")
    }

    @Scheduled(fixedDelay = 600_000) // 10분마다 재조정
    fun reconcile() {
        logger.info("REDIS:STOCK:RECONCILE - Redis 재고 재조정 시작")
        syncAllStock()
        logger.info("REDIS:STOCK:RECONCILE - Redis 재고 재조정 완료")
    }

    private fun syncAllStock() {
        val productDetails = productDetailRepository.findAll()
        productDetails.forEach { detail ->
            redisStockRepository.initializeStock(detail.id, detail.availableQuantity)
        }
        logger.info("REDIS:STOCK:SYNC - {}개 상품 재고 동기화 완료", productDetails.size)
    }
}
