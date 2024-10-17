package com.hhplus.ecommerce.infrastructure.cart

import com.hhplus.ecommerce.infrastructure.cart.jpa.CartJpaRepository
import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class CartRepository(
    private val cartJpaRepository: CartJpaRepository
): ICartRepository {
    override fun existsByProductId(productId: Long): Boolean {
        return cartJpaRepository.existsByProductId(productId)
    }

    override fun existsByUserId(userId: Long): Boolean {
        return cartJpaRepository.existsByUserId(userId)
    }

    override fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean {
        return cartJpaRepository.existsByUserIdAndProductId(userId, productId)
    }

    override fun findAllByUserId(userId: Long): List<CartEntity> {
        return cartJpaRepository.findByUserId(userId)
    }

    override fun findByProductId(productId: Long): CartEntity? {
        val result = cartJpaRepository.findByProductId(productId)

        if (result.isPresent) return result.get()
        else return null
    }

    override fun insertOrUpdate(entity: CartEntity): CartEntity {
        return cartJpaRepository.save(entity)
    }

    override fun deleteById(cartId: Long): Long {
        cartJpaRepository.deleteById(cartId)
        return cartId
    }
}