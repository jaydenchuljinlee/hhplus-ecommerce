package com.hhplus.ecommerce.common.exception.balance

class BalanceNotFoundException(message: String = "잔액 정보가 존재하지 않습니다."): BalanceException(message)