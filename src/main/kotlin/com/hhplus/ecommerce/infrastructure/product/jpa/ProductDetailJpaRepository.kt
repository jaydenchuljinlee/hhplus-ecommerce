package com.hhplus.ecommerce.infrastructure.product.jpa

import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductDetailEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProductDetailJpaRepository: JpaRepository<ProductDetailEntity, Long> {
    fun findByProductId(productId: Long): Optional<ProductDetailEntity>
}