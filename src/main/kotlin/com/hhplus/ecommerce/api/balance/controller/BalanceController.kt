package com.hhplus.ecommerce.api.balance.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.CustomErrorResponse
import com.hhplus.ecommerce.api.balance.dto.*
import com.hhplus.ecommerce.domain.balance.BalanceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("balance")
@RestController
class BalanceController(
    private val balanceService: BalanceService
) {

    @Tag(name = "잔액 기능")
    @Operation(summary = "잔액 충전 API", description = "잔액을 충전해주는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "잔액 충전 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PatchMapping("charge")
    fun charge(
        @RequestBody request: BalanceChargeRequest
    ): CustomApiResponse<BalanceTransactionResponse> {
        val result = balanceService.charge(request.toBalanceTransaction())

        return CustomApiResponse.success(BalanceTransactionResponse.from(result))
    }

    @Tag(name = "잔액 기능")
    @Operation(summary = "잔액 조회 API", description = "잔액을 조회해주는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "잔액 조회 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @GetMapping()
    fun getBalance(
        @RequestBody request: BalanceViewRequest
    ): CustomApiResponse<BalanceViewResponse> {
        val result = balanceService.getBalance(request.toQuery())
        return CustomApiResponse.success(BalanceViewResponse.from(result))
    }
}