package com.hhplus.ecommerce.cart.api

import com.hhplus.ecommerce.cart.api.dto.CartCreationRequest
import com.hhplus.ecommerce.cart.api.dto.CartResponse
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("cart")
interface ICartController {

    @Tag(name = "장바구니 기능")
    @Operation(summary = "장바구니 추가 API", description = "상품을 장바구니에 추가하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "장바구니 추가 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping()
    fun addCart(@Valid @RequestBody request: CartCreationRequest): ResponseEntity<CustomApiResponse<CartResponse>>

    @Tag(name = "장바구니 기능")
    @Operation(summary = "장바구니 삭제 API", description = "장바구니의 상품을 삭제하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "장바구니 삭제 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @DeleteMapping()
    fun deleteCart(
        @Parameter(description = "삭제할 장바구니 ID", required = true, example = "1")
        @RequestParam @Min(1) cartId: Long
    ): ResponseEntity<Void>

    @Tag(name = "장바구니 기능")
    @Operation(summary = "장바구니 목록 조회 API", description = "장바구니의 상품을 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "장바구니 목록 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("list")
    fun getCartList(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        @RequestParam @Min(1) userId: Long
    ): CustomApiResponse<List<CartResponse>>
}
