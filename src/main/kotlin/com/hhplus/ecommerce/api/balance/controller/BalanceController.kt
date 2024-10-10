package com.hhplus.ecommerce.api.balance.controller

import com.hhplus.ecommerce.api.balance.dto.BalanceRequest
import com.hhplus.ecommerce.api.balance.dto.BalanceResponse
import com.hhplus.ecommerce.common.exception.balance.BalanceLimitExceededException
import com.hhplus.ecommerce.common.exception.balance.BalanceNotFoundException
import com.hhplus.ecommerce.common.exception.user.UserNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("account")
@RestController
class BalanceController {
    private val MAX_BALANCE = 100_000_000

    @PatchMapping("charge")
    fun charge(request: BalanceRequest.Charge): BalanceResponse.Charge {

        if (request.userId == 1L) throw UserNotFoundException()

        if (request.userId == 2L) throw BalanceNotFoundException()

        if (request.amount > MAX_BALANCE) throw BalanceLimitExceededException()

        val result = BalanceResponse.Charge.getInstance()
        val newBalance = result.amount + request.amount
        if (newBalance > MAX_BALANCE) throw BalanceLimitExceededException()

        return BalanceResponse.Charge(
            userId = request.userId,
            amount = newBalance,
            transactionType = "CHARGE"
        )
    }

    @GetMapping("view")
    fun getBalance(request: BalanceRequest.View): BalanceResponse.View {
        if (request.userId == 1L) throw UserNotFoundException()

        if (request.userId == 2L) throw BalanceNotFoundException()

        return BalanceResponse.View.getInstance()
    }
}