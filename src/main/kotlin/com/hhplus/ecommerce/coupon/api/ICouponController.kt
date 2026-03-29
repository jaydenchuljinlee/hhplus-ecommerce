package com.hhplus.ecommerce.coupon.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.coupon.api.dto.CouponIssueRequest
import com.hhplus.ecommerce.coupon.api.dto.UserCouponResponse
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

@RequestMapping("/coupon")
interface ICouponController {

    @Tag(name = "쿠폰 기능")
    @Operation(summary = "쿠폰 발급 API", description = "선착순 쿠폰을 발급하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "쿠폰 발급 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "409", description = "수량 소진 또는 중복 발급", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping("/issue")
    fun issue(
        @Valid @RequestBody request: CouponIssueRequest
    ): ResponseEntity<CustomApiResponse<UserCouponResponse>>

    @Tag(name = "쿠폰 기능")
    @Operation(summary = "사용자 쿠폰 목록 조회 API", description = "사용자가 보유한 쿠폰 목록을 조회하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "잘못된 요청", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping
    fun getUserCoupons(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        @RequestParam @Min(1) userId: Long
    ): ResponseEntity<CustomApiResponse<List<UserCouponResponse>>>
}
