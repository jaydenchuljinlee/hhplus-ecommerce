package com.hhplus.ecommerce.infrastructure.balance.exception

class BalanceLimitExceededException(message: String = "잔액 충전 범위를 초과하셨습니다."): BalanceException(message)