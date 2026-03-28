package com.hhplus.ecommerce.cart.api

import com.hhplus.ecommerce.cart.api.dto.CartCreationRequest
import com.hhplus.ecommerce.cart.api.dto.CartResponse
import com.hhplus.ecommerce.cart.domain.CartService
import com.hhplus.ecommerce.cart.domain.dto.CartDeletion
import com.hhplus.ecommerce.cart.domain.dto.CartListQuery
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class CartController(
    private val cartService: CartService
) : ICartController {

    override fun addCart(request: CartCreationRequest): ResponseEntity<CustomApiResponse<CartResponse>> {
        val result = cartService.add(request.toCartCreation())
        return ResponseEntity.status(201).body(CustomApiResponse.created(CartResponse.toCartResponse(result)))
    }

    override fun deleteCart(@RequestParam @Min(1) cartId: Long): ResponseEntity<Void> {
        cartService.delete(CartDeletion(cartId))
        return ResponseEntity.noContent().build()
    }

    override fun getCartList(@RequestParam @Min(1) userId: Long): CustomApiResponse<List<CartResponse>> {
        val results = cartService.getCartList(CartListQuery(userId))
        return CustomApiResponse.success(results.map { CartResponse.toCartResponse(it) })
    }
}
