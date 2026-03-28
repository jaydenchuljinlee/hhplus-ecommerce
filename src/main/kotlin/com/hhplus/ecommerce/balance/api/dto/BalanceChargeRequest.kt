package com.hhplus.ecommerce.balance.api.dto

import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction
import com.hhplus.ecommerce.balance.domain.dto.BalanceTransaction.TransactionType
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class BalanceChargeRequest(
    @field:Min(1, message = "userId는 1 이상이어야 합니다.")
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @field:Min(1, message = "충전 금액은 1원 이상이어야 합니다.")
    @Parameter(description = "충전할 금액", required = true)
    @Schema(description = "충전할 금액", example = "1000")
    var amount: Long,
) {
    fun toBalanceTransaction(): BalanceTransaction {
        return BalanceTransaction(
            userId = userId,
            amount = amount,
            type = TransactionType.CHARGE
        )
    }
}
