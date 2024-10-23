package com.hhplus.ecommerce.infrastructure.balance.exception

class InsufficientBalanceException(message: String = "잔액이 부족합니다."): BalanceException(message)