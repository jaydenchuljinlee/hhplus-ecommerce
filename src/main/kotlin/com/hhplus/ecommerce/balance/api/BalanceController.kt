package com.hhplus.ecommerce.balance.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.balance.api.dto.BalanceChargeRequest
import com.hhplus.ecommerce.balance.api.dto.BalanceTransactionResponse
import com.hhplus.ecommerce.balance.api.dto.BalanceViewRequest
import com.hhplus.ecommerce.balance.api.dto.BalanceViewResponse
import com.hhplus.ecommerce.balance.domain.BalanceService
import org.springframework.web.bind.annotation.*

@RestController
class BalanceController(
    private val balanceService: BalanceService
): IBalanceController {
    override fun charge(request: BalanceChargeRequest): CustomApiResponse<BalanceTransactionResponse> {
        val result = balanceService.charge(request.toBalanceTransaction())

        return CustomApiResponse.success(BalanceTransactionResponse.from(result))
    }


    override fun getBalance(request: BalanceViewRequest): CustomApiResponse<BalanceViewResponse> {
        val result = balanceService.getBalance(request.toQuery())
        return CustomApiResponse.success(BalanceViewResponse.from(result))
    }
}