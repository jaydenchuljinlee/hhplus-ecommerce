package com.hhplus.ecommerce.api.balance.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.balance.dto.*
import com.hhplus.ecommerce.common.exception.balance.BalanceLimitExceededException
import com.hhplus.ecommerce.common.exception.balance.BalanceNotFoundException
import com.hhplus.ecommerce.common.exception.user.UserNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*

@RequestMapping("balance")
@RestController
class BalanceController {
    private val MAX_BALANCE = 100_000_000

    @Operation(summary = "잔액 충전", description = "잔액 충전 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "잔액 충전 요청 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @PatchMapping("charge")
    fun charge(
        @RequestBody request: BalanceChargeRequest
    ): CustomApiResponse<BalanceTransactionResponse> {

        if (request.userId == 1L) throw UserNotFoundException()

        if (request.userId == 2L) throw BalanceNotFoundException()

        if (request.amount > MAX_BALANCE) throw BalanceLimitExceededException()

        val result = BalanceTransactionResponse.getInstance()
        val newBalance = result.amount + request.amount
        if (newBalance > MAX_BALANCE) throw BalanceLimitExceededException()

        val response = BalanceTransactionResponse(
            userId = request.userId,
            amount = newBalance,
            transactionType = "CHARGE"
        )

        return CustomApiResponse.success(response)
    }

    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "잔액 조회 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @GetMapping()
    fun getBalance(
        @RequestBody request: BalanceViewRequest
    ): CustomApiResponse<BalanceViewResponse> {
        if (request.userId == 1L) throw UserNotFoundException()

        if (request.userId == 2L) throw BalanceNotFoundException()

        return CustomApiResponse.success(BalanceViewResponse.getInstance())
    }
}