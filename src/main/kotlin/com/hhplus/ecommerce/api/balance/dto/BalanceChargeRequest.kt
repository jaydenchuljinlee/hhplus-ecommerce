package com.hhplus.ecommerce.api.balance.dto

import com.hhplus.ecommerce.api.balance.controller.exception.InsufficientBalanceAmountException
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction
import com.hhplus.ecommerce.domain.balance.dto.BalanceTransaction.TransactionType
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema

data class BalanceChargeRequest(
    @Parameter(description = "사용자 ID", required = true)
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Parameter(description = "충전할 금액", required = true)
    @Schema(description = "충전할 금액", example = "1000")
    var amount: Long,
) {
    init {
        require(amount >= 0) { throw InsufficientBalanceAmountException() }
    }

    fun toBalanceTransaction(): BalanceTransaction {
        return BalanceTransaction(
            userId = userId,
            amount = amount,
            type = TransactionType.CHARGE
        )
    }
}