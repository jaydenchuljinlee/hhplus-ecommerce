package com.hhplus.ecommerce.order.api

import com.hhplus.ecommerce.common.anotation.RateLimit
import com.hhplus.ecommerce.order.api.dto.OrderCreationRequest
import com.hhplus.ecommerce.order.api.dto.OrderResponse
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/v1/order")
interface IOrderController {
    @Tag(name = "주문 기능")
    @Operation(summary = "주문 API", description = "주문 요청을 처리하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "주문 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "429", description = "요청 한도 초과", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping()
    @RateLimit(key = "#request.userId", limit = 20, seconds = 60)
    fun prepareOrder(
        @RequestBody request: OrderCreationRequest
    ): CustomApiResponse<OrderResponse>

    @Tag(name = "주문 기능")
    @Operation(summary = "주문 API (Redisson 분산락)", description = "Redisson 분산락 기반 재고 점유 주문 API (Lua 스크립트 방식과 성능 비교용)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "주문 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "429", description = "요청 한도 초과", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping("/lock")
    @RateLimit(key = "#request.userId", limit = 20, seconds = 60)
    fun prepareOrderWithLock(
        @RequestBody request: OrderCreationRequest
    ): CustomApiResponse<OrderResponse>
}