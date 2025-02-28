package com.hhplus.ecommerce.balance.infrastructure.exception

class BalanceLimitExceededException(message: String = "잔액 충전 범위를 초과하셨습니다."): BalanceException(message)