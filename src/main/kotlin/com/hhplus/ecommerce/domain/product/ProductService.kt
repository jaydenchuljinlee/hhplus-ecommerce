package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.domain.product.dto.*
import com.hhplus.ecommerce.domain.product.repository.IProductRepository
import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import com.hhplus.ecommerce.infrastructure.redis.PubSubLockSupporter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ProductService(
    private val productRepository: IProductRepository,
    private val pubSubLockSupporter: PubSubLockSupporter
) {
    fun getProduct(dto: ProductInfoQuery): ProductInfoResult {
        val productDetailEntity = productRepository.findByProductId(dto.productId)
        val productEntity = productRepository.findById(dto.productId)

        val result = ProductInfoResult(
            productId = productEntity.id,
            productName = productEntity.name,
            price = productEntity.price,
            stock = productDetailEntity.quantity
        )
        return result
    }


    fun decreaseStock(dto: DecreaseProductDetailStock): ProductDetailResult {
        val productKey = "product:${dto.id}"

        val lock = pubSubLockSupporter.getLock(productKey)

        try {
            val acquireLock = lock.tryLock(10, 1, TimeUnit.SECONDS)
            if (!acquireLock) {
                throw InterruptedException("Lock 획득 실패")
            }

            val productDetailEntity = productRepository.decreaseStock(dto.id, dto.amount)

            return ProductDetailResult.from(productDetailEntity)

        } finally {
            lock.unlock()
        }
    }

    @Transactional
    fun getProductDetail(item: ProductDetailQuery): ProductDetailResult {
        val entity = productRepository.findByProductId(item.productId)
        return ProductDetailResult.from(entity)
    }

    fun getTopFiveLastThreeDays(): List<BestSellingProduct> {
        return productRepository.findTopFiveLastThreeDays()
    }

}