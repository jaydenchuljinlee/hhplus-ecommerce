package com.hhplus.ecommerce.infrastructure.balance.exception

import com.hhplus.ecommerce.common.exception.balance.BalanceException

class InsufficientBalanceException(message: String = "잔액이 부족합니다."): BalanceException(message)