package com.hhplus.ecommerce.infrastructure.product.jpa

import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductOptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductOptionJpaRepository: JpaRepository<ProductOptionEntity, Long> {
}