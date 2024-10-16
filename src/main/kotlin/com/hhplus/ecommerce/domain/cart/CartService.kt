package com.hhplus.ecommerce.domain.cart

import com.hhplus.ecommerce.domain.cart.dto.CartCreation
import com.hhplus.ecommerce.domain.cart.dto.CartDeletion
import com.hhplus.ecommerce.domain.cart.dto.CartResult
import com.hhplus.ecommerce.domain.cart.dto.ProductIdCartQuery
import com.hhplus.ecommerce.infrastructure.cart.ICartRepository
import com.hhplus.ecommerce.infrastructure.cart.jpa.entity.CartEntity
import org.springframework.stereotype.Service

@Service
class CartService(
    private val cartRepository: ICartRepository
) {
    fun getCartByProduct(dto: ProductIdCartQuery): CartResult? {
        val cartEntity = cartRepository.findByProductId(dto.productId)

        return if (cartEntity != null) CartResult.from(cartEntity)
        else null
    }

    fun add(dto: CartCreation): CartEntity {
        val entity = dto.toEntity()

        val result = cartRepository.insertOrUpdate(entity)

        return result
    }

    fun delete(dto: CartDeletion): Long {
        return cartRepository.deleteById(dto.cartId)
    }
}