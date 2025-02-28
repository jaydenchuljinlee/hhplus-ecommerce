package com.hhplus.ecommerce.balance.api

import com.hhplus.ecommerce.balance.api.dto.BalanceChargeRequest
import com.hhplus.ecommerce.balance.api.dto.BalanceTransactionResponse
import com.hhplus.ecommerce.balance.api.dto.BalanceViewRequest
import com.hhplus.ecommerce.balance.api.dto.BalanceViewResponse
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("balance")
interface IBalanceController {

    @Tag(name = "잔액 기능")
    @Operation(summary = "잔액 충전 API", description = "잔액을 충전해주는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "잔액 충전 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "400", description = "충전 잔액 Bad Request 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PatchMapping("charge")
    fun charge(@RequestBody request: BalanceChargeRequest): CustomApiResponse<BalanceTransactionResponse>

    @Tag(name = "잔액 기능")
    @Operation(summary = "잔액 조회 API", description = "잔액을 조회해주는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "잔액 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping()
    fun getBalance(
        @RequestBody request: BalanceViewRequest
    ): CustomApiResponse<BalanceViewResponse>
}