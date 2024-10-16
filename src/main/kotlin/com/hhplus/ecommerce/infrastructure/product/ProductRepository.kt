package com.hhplus.ecommerce.infrastructure.product

import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import com.hhplus.ecommerce.infrastructure.product.jpa.ProductDetailJpaRepository
import com.hhplus.ecommerce.infrastructure.product.jpa.ProductJpaRepository
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductEntity
import com.hhplus.ecommerce.infrastructure.product.jpa.querydsl.IProductQueryDsl
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductRepository(
    private val productJpaRepository: ProductJpaRepository,
    private val productDetailJpaRepository: ProductDetailJpaRepository,
    private val productQueryDsl: IProductQueryDsl
): IProductRepository {

    @Transactional
    override fun findByProductIdWithLock(productId: Long): ProductDetailEntity {
        return productDetailJpaRepository.findByProductId(productId).orElseThrow { RuntimeException("상세 정보를 찾을 수 없습니다.") }
    }

    override fun findById(productId: Long): ProductEntity {
        return productJpaRepository.findById(productId).orElseThrow { ProductNotFoundException() }
    }

    override fun insertOrUpdate(entity: ProductEntity): ProductEntity {
        return productJpaRepository.save(entity)
    }

    override fun findTopFiveLastThreeDays(): List<BestSellingProduct> {
        return productQueryDsl.findTop5BestSellingProductsLast3Days()
    }

    override fun decreaseStock(productDetailId: Long, amount: Int): ProductDetailEntity {
        val productDetailEntity = productDetailJpaRepository.findById(productDetailId).orElseThrow { RuntimeException("상세 정보를 찾을 수 없습니다.") }

        productDetailEntity.decrease(amount)

        productDetailJpaRepository.save(productDetailEntity)

        return productDetailEntity
    }
}