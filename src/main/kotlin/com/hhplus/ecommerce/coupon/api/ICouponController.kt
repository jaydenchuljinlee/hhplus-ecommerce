package com.hhplus.ecommerce.coupon.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.coupon.api.dto.CouponIssueRequest
import com.hhplus.ecommerce.coupon.api.dto.UserCouponResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("coupon")
interface ICouponController {

    @Tag(name = "쿠폰 기능")
    @Operation(summary = "쿠폰 발급 API", description = "선착순 쿠폰을 발급합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "쿠폰 발급 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "409", description = "이미 발급된 쿠폰", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "400", description = "쿠폰 재고 없음", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping("{couponPolicyId}/issue")
    fun issue(
        @PathVariable couponPolicyId: Long,
        @RequestBody request: CouponIssueRequest
    ): CustomApiResponse<UserCouponResponse>

    @Tag(name = "쿠폰 기능")
    @Operation(summary = "내 쿠폰 목록 조회 API", description = "사용자의 보유 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "쿠폰 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping("my")
    fun getMyCoupons(@RequestParam userId: Long): CustomApiResponse<List<UserCouponResponse>>
}
