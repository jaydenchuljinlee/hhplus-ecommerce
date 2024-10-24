package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.domain.product.dto.*
import com.hhplus.ecommerce.domain.product.repository.IProductRepository
import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: IProductRepository
) {
    fun getProduct(dto: ProductInfoQuery): ProductInfoResult {
        val productDetailEntity = productRepository.findByProductIdWithLock(dto.productId)
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
        val productDetailEntity = productRepository.decreaseStock(dto.id, dto.amount)

        return ProductDetailResult.from(productDetailEntity)
    }

    @Transactional
    fun getProductDetailByIdWithLock(item: ProductDetailQuery): ProductDetailResult {
        val entity = productRepository.findByProductIdWithLock(item.productId)
        return ProductDetailResult.from(entity)
    }

    fun getTopFiveLastThreeDays(): List<BestSellingProduct> {
        return productRepository.findTopFiveLastThreeDays()
    }

}