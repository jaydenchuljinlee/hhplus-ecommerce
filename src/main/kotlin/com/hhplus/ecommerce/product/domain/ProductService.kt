package com.hhplus.ecommerce.product.domain

import com.hhplus.ecommerce.common.anotation.RedisCacheable
import com.hhplus.ecommerce.product.domain.repository.IProductRepository
import com.hhplus.ecommerce.product.infrastructure.dto.BestSellingProduct
import com.hhplus.ecommerce.product.domain.dto.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: IProductRepository,
) {
    fun getProduct(dto: ProductInfoQuery): ProductInfoResult {
        val productDetailEntity = productRepository.findByProductId(dto.productId)
        val productEntity = productRepository.findById(dto.productId)

        return ProductInfoResult(
            productId = productEntity.id,
            productName = productEntity.name,
            price = productEntity.price,
            stock = productDetailEntity.quantity
        )
    }

    @RedisCacheable(key = "'product:cache:' + #dto.productId")
    fun getProductCache(dto: ProductInfoQuery): ProductInfoResult {
        return getProduct(dto)
    }

    fun getProductDB(dto: ProductInfoQuery): ProductInfoResult {
        return getProduct(dto)
    }

    fun decreaseStock(dto: DecreaseProductDetailStock): ProductDetailResult {
        val productDetailEntity = productRepository.decreaseStock(dto.id, dto.amount)

        return ProductDetailResult.from(productDetailEntity)
    }

    fun getProductDetail(item: ProductDetailQuery): ProductDetailResult {
        val entity = productRepository.findByProductId(item.productId)
        return ProductDetailResult.from(entity)
    }

    fun getTopFiveLastThreeDaysFromDB(): List<BestSellingProduct> {
        return productRepository.findTopFiveLastThreeDays()
    }

    @Cacheable("bestSellingProducts:last3days")
    fun getTopFiveLastThreeDaysFromCache(): List<BestSellingProduct> {
        return productRepository.findTopFiveLastThreeDays()
    }

    fun deleteCache(productId: Long) {
        productRepository.invalidateProductCache(productId)
    }

    fun refreshTopFiveLastThreeDays() {
        productRepository.refreshBestSellersCache()
    }
}
