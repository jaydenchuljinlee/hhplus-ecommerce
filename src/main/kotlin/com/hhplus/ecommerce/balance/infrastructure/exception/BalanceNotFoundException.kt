package com.hhplus.ecommerce.balance.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class BalanceNotFoundException(message: String = "잔액 정보가 존재하지 않습니다."): NotFoundException(message)