package com.hhplus.ecommerce.api.balance.controller

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.api.balance.dto.*
import com.hhplus.ecommerce.domain.balance.BalanceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
class BalanceController(
    private val balanceService: BalanceService
): IBalanceController{
    override fun charge(request: BalanceChargeRequest): CustomApiResponse<BalanceTransactionResponse> {
        val result = balanceService.charge(request.toBalanceTransaction())

        return CustomApiResponse.success(BalanceTransactionResponse.from(result))
    }


    override fun getBalance(request: BalanceViewRequest): CustomApiResponse<BalanceViewResponse> {
        val result = balanceService.getBalance(request.toQuery())
        return CustomApiResponse.success(BalanceViewResponse.from(result))
    }
}