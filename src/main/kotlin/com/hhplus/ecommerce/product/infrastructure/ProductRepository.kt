package com.hhplus.ecommerce.product.infrastructure

import com.hhplus.ecommerce.product.infrastructure.exception.ProductNotFoundException
import com.hhplus.ecommerce.product.domain.repository.IProductRepository
import com.hhplus.ecommerce.product.infrastructure.dto.BestSellingProduct
import com.hhplus.ecommerce.product.infrastructure.jpa.ProductDetailJpaRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.ProductJpaRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductEntity
import com.hhplus.ecommerce.product.infrastructure.jpa.querydsl.IProductQueryDsl
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductRepository(
    private val productJpaRepository: ProductJpaRepository,
    private val productDetailJpaRepository: ProductDetailJpaRepository,
    private val productQueryDsl: IProductQueryDsl
): IProductRepository {

    @Transactional
    override fun findByProductId(productId: Long): ProductDetailEntity {
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

    @Transactional
    override fun decreaseStock(productDetailId: Long, amount: Int): ProductDetailEntity {
        val productDetailEntity = productDetailJpaRepository.findById(productDetailId).orElseThrow { RuntimeException("상세 정보를 찾을 수 없습니다.") }

        productDetailEntity.decrease(amount)
        println(productDetailEntity.quantity)
        productDetailJpaRepository.save(productDetailEntity)

        return productDetailEntity
    }
}