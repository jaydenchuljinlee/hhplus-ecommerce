package com.hhplus.ecommerce.api.product.controller

import com.hhplus.ecommerce.api.product.dto.BestSellingTopFiveResponse
import com.hhplus.ecommerce.api.product.dto.ProductInfoQueryRequest
import com.hhplus.ecommerce.api.product.dto.ProductInfoResponse
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
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

@RequestMapping("/product")
interface IProductController {
    @Tag(name = "상품 기능")
    @Operation(summary = "상품 조회 API (캐시)", description = "상품을 캐시에서 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "상품 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("/cache")
    fun getProductFromCache(
        @Parameter(description = "상품 조회 요청 정보", required = true)
        request: ProductInfoQueryRequest
    ): CustomApiResponse<ProductInfoResponse>

    @Tag(name = "상품 기능")
    @Operation(summary = "상품 조회 API (DB)", description = "상품을 DB에서 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "상품 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("/db")
    fun getProductFromDB(
        @Parameter(description = "상품 조회 요청 정보", required = true)
        request: ProductInfoQueryRequest
    ): CustomApiResponse<ProductInfoResponse>

    @Tag(name = "상품 기능")
    @Operation(summary = "베스트 주문 TOP5 상품 조회 API (캐시)", description = "베스트 주문 TOP5 상품을 캐시에서 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "베스트 주문 TOP5 상품 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("list/top_five/cache")
    fun getTopFiveLastThreeDaysFromCache(): CustomApiResponse<List<BestSellingTopFiveResponse>>

    @Tag(name = "상품 기능")
    @Operation(summary = "베스트 주문 TOP5 상품 조회 API (DB)", description = "베스트 주문 TOP5 상품을 DB에서 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "베스트 주문 TOP5 상품 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("list/top_five/db")
    fun getTopFiveLastThreeDaysFromDB(): CustomApiResponse<List<BestSellingTopFiveResponse>>
}