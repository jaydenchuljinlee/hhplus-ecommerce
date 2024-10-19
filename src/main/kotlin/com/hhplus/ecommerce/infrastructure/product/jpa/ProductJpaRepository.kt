package com.hhplus.ecommerce.infrastructure.product.jpa

import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository: JpaRepository<ProductEntity, Long> {
}