package com.hhplus.ecommerce.balance.api

import com.hhplus.ecommerce.balance.api.dto.BalanceChargeRequest
import com.hhplus.ecommerce.balance.api.dto.BalanceTransactionResponse
import com.hhplus.ecommerce.balance.api.dto.BalanceViewResponse
import com.hhplus.ecommerce.balance.domain.BalanceService
import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class BalanceController(
    private val balanceService: BalanceService
) : IBalanceController {

    override fun charge(request: BalanceChargeRequest): CustomApiResponse<BalanceTransactionResponse> {
        val result = balanceService.charge(request.toBalanceTransaction())
        return CustomApiResponse.success(BalanceTransactionResponse.from(result))
    }

    override fun getBalance(@RequestParam @Min(1) userId: Long): CustomApiResponse<BalanceViewResponse> {
        val result = balanceService.getBalance(BalanceQuery(userId))
        return CustomApiResponse.success(BalanceViewResponse.from(result))
    }
}
