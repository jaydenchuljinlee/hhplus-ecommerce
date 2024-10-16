package com.hhplus.ecommerce.api.balance.dto

import io.swagger.v3.oas.annotations.Parameter

data class BalanceChargeRequest(
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
    @Parameter(description = "충전할 금액", required = true)
    var amount: Int,
)