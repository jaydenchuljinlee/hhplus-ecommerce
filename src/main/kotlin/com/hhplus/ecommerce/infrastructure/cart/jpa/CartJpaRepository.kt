package com.hhplus.ecommerce.infrastructure.cart.jpa

import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CartJpaRepository: JpaRepository<CartEntity, Long> {
    fun existsByProductId(productId: Long): Boolean
    fun existsByUserId(userId: Long): Boolean
    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findByUserId(userId: Long): List<CartEntity>
    fun findByProductId(productId: Long): Optional<CartEntity>
}