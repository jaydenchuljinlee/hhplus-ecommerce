package com.hhplus.ecommerce.infrastructure.product.jpa

import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductDetailEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.Optional

interface ProductDetailJpaRepository: JpaRepository<ProductDetailEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByProductId(productId: Long): Optional<ProductDetailEntity>
}