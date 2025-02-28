package com.hhplus.ecommerce.product.infrastructure.jpa

import com.hhplus.ecommerce.product.infrastructure.jpa.entity.ProductOptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductOptionJpaRepository: JpaRepository<ProductOptionEntity, Long> {
}