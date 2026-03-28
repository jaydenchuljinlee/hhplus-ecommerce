package com.hhplus.ecommerce.balance.api.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceQuery
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class BalanceViewRequest(
    @field:Min(1, message = "userId는 1 이상이어야 합니다.")
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
) {
    fun toQuery(): BalanceQuery {
        return BalanceQuery(userId)
    }
}