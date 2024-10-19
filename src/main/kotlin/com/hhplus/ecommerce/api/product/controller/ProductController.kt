package com.hhplus.ecommerce.api.product.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.CustomErrorResponse
import com.hhplus.ecommerce.api.product.dto.*
import com.hhplus.ecommerce.domain.product.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/product")
@RestController
class ProductController(
    private val productService: ProductService
) {

    @Tag(name = "상품 기능")
    @Operation(summary = "상품 조회 API", description = "상품을 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "상품 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping()
    fun getProduct(
        @Parameter(description = "상품 조회 요청 정보", required = true)
        @RequestBody request: ProductInfoQueryRequest
    ): CustomApiResponse<ProductInfoResponse> {
        val result = productService.getProduct(request.toProductInfoQuery())

        return CustomApiResponse.success(ProductInfoResponse.from(result))
    }

    @Tag(name = "상품 기능")
    @Operation(summary = "베스트 주문 TOP5 상품 조회 API", description = "베스트 주문 TOP5 상품을 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "베스트 주문 TOP5 상품 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("list/top_five")
    fun getProductTopFive(): CustomApiResponse<List<BestSellingTopFiveResponse>> {
        val result = productService.getTopFiveLastThreeDays()
        val response = result.map { BestSellingTopFiveResponse.from(it) }
        return CustomApiResponse.success(response)
    }
}