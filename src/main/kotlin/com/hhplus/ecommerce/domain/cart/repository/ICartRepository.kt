package com.hhplus.ecommerce.domain.cart.repository

import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity

interface ICartRepository {
    fun existsByProductId(productId: Long): Boolean
    fun existsByUserId(userId: Long): Boolean
    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findAllByUserId(userId: Long): List<CartEntity>
    fun findByProductId(productId: Long): CartEntity?

    fun insertOrUpdate(entity: CartEntity): CartEntity
    fun deleteById(cartId: Long): Long
}