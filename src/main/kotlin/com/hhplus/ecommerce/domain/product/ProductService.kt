package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.domain.product.dto.*
import com.hhplus.ecommerce.infrastructure.product.IProductRepository
import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: IProductRepository
) {

    fun decreaseStock(item: DecreaseProductDetailStock): ProductDetailResult {
        val productDetailEntity = productRepository.decreaseStock(item.id, item.amount)

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