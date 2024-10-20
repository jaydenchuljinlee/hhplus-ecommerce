package com.hhplus.ecommerce.infrastructure.balance.exception

import com.hhplus.ecommerce.common.exception.balance.BalanceException

class BalanceNotFoundException(message: String = "잔액 정보가 존재하지 않습니다."): BalanceException(message)