package com.hhplus.ecommerce.domain.cart

import com.hhplus.ecommerce.domain.cart.dto.*
import com.hhplus.ecommerce.infrastructure.cart.ICartRepository
import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity
import org.springframework.stereotype.Service

@Service
class CartService(
    private val cartRepository: ICartRepository
) {
    fun getCartList(dto: CartListQuery): List<CartResult> {
        val result = cartRepository.findAllByUserId(dto.userId)
        return result.map { CartResult.from(it) }
    }

    fun getCartByProduct(dto: ProductIdCartQuery): CartResult? {
        val cartEntity = cartRepository.findByProductId(dto.productId)

        return if (cartEntity != null) CartResult.from(cartEntity)
        else null
    }

    fun add(dto: CartCreation): CartResult {
        val entity = dto.toEntity()

        val cartEntity = cartRepository.insertOrUpdate(entity)

        return CartResult.from(cartEntity)
    }

    fun delete(dto: CartDeletion): Long {
        return cartRepository.deleteById(dto.cartId)
    }
}