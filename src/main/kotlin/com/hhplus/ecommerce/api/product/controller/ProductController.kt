package com.hhplus.ecommerce.api.product.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.product.dto.*
import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/product")
@RestController
class ProductController {

    @Operation(summary = "상품 조회", description = "상품 조회 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "상품 조회 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @GetMapping()
    fun getProduct(
        @Parameter(description = "상품 조회 요청 정보", required = true)
        @RequestBody request: ProductViewRequest
    ): CustomApiResponse<ProductDetailResponse> {

        if (request.productId == 1L) throw ProductNotFoundException()

        return CustomApiResponse.success(ProductDetailResponse.getInstance())
    }

    @Operation(summary = "베스트 주문 TOP5 상품 조회", description = "베스트 주문 TOP5 상품 조회 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "베스트 주문 TOP5 상품 조회 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @GetMapping("list/top_five")
    fun getProductTopFive(): CustomApiResponse<BestSellingTopFiveResponse> {
        return CustomApiResponse.success(BestSellingTopFiveResponse.getInstance())
    }
}