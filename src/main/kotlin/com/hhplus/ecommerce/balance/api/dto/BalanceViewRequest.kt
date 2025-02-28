package com.hhplus.ecommerce.balance.api.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

data class BalanceViewRequest(
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
) {
    fun toQuery(): BalanceQuery {
        return BalanceQuery(userId)
    }
}