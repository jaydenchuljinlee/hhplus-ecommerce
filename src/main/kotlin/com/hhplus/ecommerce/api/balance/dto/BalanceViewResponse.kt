package com.hhplus.ecommerce.api.balance.dto

import com.hhplus.ecommerce.domain.balance.dto.BalanceResult
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
        fun getInstance() = BalanceViewResponse(
            userId = 0L,
            balance = 1000L
        )

        fun from(result: BalanceResult): BalanceViewResponse {
            return BalanceViewResponse(
                userId = result.userId,
                balance = result.balance,
            )
        }
    }
}