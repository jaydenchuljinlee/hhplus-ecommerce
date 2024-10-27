package com.hhplus.ecommerce.api.balance.controller.exception

import com.hhplus.ecommerce.common.exception.ControllerException

class InsufficientBalanceAmountException(message: String = "충전 금액은 0보다 커야합니다."): ControllerException(message)