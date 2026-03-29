package com.hhplus.ecommerce.product.infrastructure.jpa

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductDetailEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface ProductDetailJpaRepository: JpaRepository<ProductDetailEntity, Long> {
    fun findByProductId(productId: Long): Optional<ProductDetailEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductDetailEntity p WHERE p.id = :id")
    fun findByIdWithLock(id: Long): Optional<ProductDetailEntity>
}