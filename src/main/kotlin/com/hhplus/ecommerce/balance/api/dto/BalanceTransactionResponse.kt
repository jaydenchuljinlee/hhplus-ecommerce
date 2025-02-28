package com.hhplus.ecommerce.balance.api.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

data class BalanceTransactionResponse(
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Parameter(description = "충전 금액", required = true)
    @Schema(description = "충전할 금액", example = "1000")
    var amount: Long
) {
    companion object {
        fun from(result: BalanceResult): BalanceTransactionResponse {
            return BalanceTransactionResponse(
                userId = result.userId,
                amount = result.balance,
            )
        }
    }
}