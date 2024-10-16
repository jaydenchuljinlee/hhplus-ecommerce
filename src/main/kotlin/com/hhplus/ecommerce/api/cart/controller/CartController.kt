package com.hhplus.ecommerce.api.cart.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.cart.dto.CartCreationRequest
import com.hhplus.ecommerce.api.cart.dto.CartDeletionRequest
import com.hhplus.ecommerce.api.cart.dto.CartRequest
import com.hhplus.ecommerce.api.cart.dto.CartResponse
import com.hhplus.ecommerce.common.exception.cart.CartNotFoundException
import com.hhplus.ecommerce.common.exception.cart.DuplicatedProductException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*

@RequestMapping("cart")
@RestController
class CartController {

    @Operation(summary = "장바구니 추가", description = "장바구니 추가 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 추가 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @PostMapping()
    fun addCart(
        @RequestBody request: CartCreationRequest
    ): CustomApiResponse<CartResponse> {
        // 중복 체크
        if (request.productId == 1L) throw DuplicatedProductException()

        return CustomApiResponse.success(CartResponse.getInstance())
    }

    @Operation(summary = "장바구니 삭제", description = "장바구니 삭제 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 삭제 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @DeleteMapping()
    fun deleteCart(
        @Parameter(description = "삭제할 장바구니 정보", required = true)
        @RequestBody request: CartDeletionRequest
    ): CustomApiResponse<Long> {
        // 존재 여부 체크
        if (request.cartId == 1L) throw CartNotFoundException()

        return CustomApiResponse.success(0L)
    }

    @Operation(summary = "장바구니 목록 조회", description = "장바구니 목록 조회 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 목록 조회 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @GetMapping("list")
    fun getCartList(): CustomApiResponse<List<CartResponse>> {
        val results = listOf(CartResponse.getInstance())
        return CustomApiResponse.success(results)
    }
}