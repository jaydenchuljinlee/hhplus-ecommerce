package com.hhplus.ecommerce.balance.api.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceResult
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

class BalanceViewResponse(
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Parameter(description = "현재 잔액", required = true)
    @Schema(description = "현재 잔액", example = "1000")
    var balance: Long
) {
    companion object {

        fun from(result: BalanceResult): BalanceViewResponse {
            return BalanceViewResponse(
                userId = result.userId,
                balance = result.balance,
            )
        }
    }
}