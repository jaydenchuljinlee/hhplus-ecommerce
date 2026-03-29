package com.hhplus.ecommerce.payment.domain.dto

import com.hhplus.ecommerce.payment.common.PayMethod

data class CreationPaymentCommand(
    var orderId: Long,
    var userId: Long,
    var price: Long,
    var payMethod: PayMethod = PayMethod.BALANCE
)