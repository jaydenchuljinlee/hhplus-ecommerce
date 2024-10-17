package com.hhplus.ecommerce.api.cart.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.CustomErrorResponse
import com.hhplus.ecommerce.api.cart.dto.CartCreationRequest
import com.hhplus.ecommerce.api.cart.dto.CartDeletionRequest
import com.hhplus.ecommerce.api.cart.dto.CartListQueryRequest
import com.hhplus.ecommerce.api.cart.dto.CartResponse
import com.hhplus.ecommerce.domain.cart.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("cart")
@RestController
class CartController(
    private val cartService: CartService
) {

    @Tag(name = "장바구니 기능")
    @Operation(summary = "장바구니 추가 API", description = "상품을 장바구니에 추가하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 추가 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping()
    fun addCart(
        @RequestBody request: CartCreationRequest
    ): CustomApiResponse<CartResponse> {
        val result = cartService.add(request.toCartCreation())

        return CustomApiResponse.success(CartResponse.toCartResponse(result))
    }

    @Tag(name = "장바구니 기능")
    @Operation(summary = "장바구니 삭제 API", description = "장바구니의 상품을 삭제하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 삭제 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @DeleteMapping()
    fun deleteCart(
        @Parameter(description = "삭제할 장바구니 정보", required = true)
        @RequestBody request: CartDeletionRequest
    ): CustomApiResponse<Long> {
        val result = cartService.delete(request.toCartDeletion())

        return CustomApiResponse.success(result)
    }

    @Tag(name = "장바구니 기능")
    @Operation(summary = "장바구니 목록 조회 API", description = "장바구니의 상품을 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 목록 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("list")
    fun getCartList(request: CartListQueryRequest): CustomApiResponse<List<CartResponse>> {
        val results = cartService.getCartList(request.toCartListQuery())
        val response = results.map { CartResponse.toCartResponse(it) }
        return CustomApiResponse.success(response)
    }
}