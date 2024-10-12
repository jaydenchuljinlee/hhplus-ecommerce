package com.hhplus.ecommerce.api.balance.controller

import com.hhplus.ecommerce.api.ApiResponse
import com.hhplus.ecommerce.api.balance.dto.BalanceRequest
import com.hhplus.ecommerce.api.balance.dto.BalanceResponse
import com.hhplus.ecommerce.common.exception.balance.BalanceLimitExceededException
import com.hhplus.ecommerce.common.exception.balance.BalanceNotFoundException
import com.hhplus.ecommerce.common.exception.user.UserNotFoundException
import org.springframework.web.bind.annotation.*

@RequestMapping("balance")
@RestController
class BalanceController {
    private val MAX_BALANCE = 100_000_000

    @PatchMapping("charge")
    fun charge(@RequestBody request: BalanceRequest.Charge): ApiResponse<BalanceResponse.Charge> {

        if (request.userId == 1L) throw UserNotFoundException()

        if (request.userId == 2L) throw BalanceNotFoundException()

        if (request.amount > MAX_BALANCE) throw BalanceLimitExceededException()

        val result = BalanceResponse.Charge.getInstance()
        val newBalance = result.amount + request.amount
        if (newBalance > MAX_BALANCE) throw BalanceLimitExceededException()

        val response = BalanceResponse.Charge(
            userId = request.userId,
            amount = newBalance,
            transactionType = "CHARGE"
        )

        return ApiResponse.success(response)
    }

    @GetMapping()
    fun getBalance(@RequestBody request: BalanceRequest.View): ApiResponse<BalanceResponse.View> {
        if (request.userId == 1L) throw UserNotFoundException()

        if (request.userId == 2L) throw BalanceNotFoundException()

        return ApiResponse.success(BalanceResponse.View.getInstance())
    }
}