package com.hhplus.ecommerce.product.infrastructure.jpa

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository: JpaRepository<ProductEntity, Long> {
}