package com.hhplus.ecommerce.common.exception.balance

class InsufficientBalanceException(message: String = "잔액이 부족합니다."): BalanceException(message)