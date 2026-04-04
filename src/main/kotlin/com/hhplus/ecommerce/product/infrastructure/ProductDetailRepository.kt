package com.hhplus.ecommerce.product.infrastructure

import com.hhplus.ecommerce.product.domain.repository.IProductDetailRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.ProductDetailJpaRepository
import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductDetailRepository(
    private val productDetailJpaRepository: ProductDetailJpaRepository
) : IProductDetailRepository {

    @Transactional(readOnly = true)
    override fun findById(productDetailId: Long): ProductDetailEntity {
        return productDetailJpaRepository.findById(productDetailId)
            .orElseThrow { RuntimeException("상품 상세 정보를 찾을 수 없습니다. id=$productDetailId") }
    }

    @Transactional
    override fun findByIdForUpdate(productDetailId: Long): ProductDetailEntity {
        return productDetailJpaRepository.findByIdWithLock(productDetailId)
            .orElseThrow { RuntimeException("상품 상세 정보를 찾을 수 없습니다. id=$productDetailId") }
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<ProductDetailEntity> {
        return productDetailJpaRepository.findAll()
    }

    @Transactional
    override fun save(entity: ProductDetailEntity?): ProductDetailEntity {
        requireNotNull(entity) { "ProductDetailEntity must not be null" }
        return productDetailJpaRepository.save(entity)
    }
}
