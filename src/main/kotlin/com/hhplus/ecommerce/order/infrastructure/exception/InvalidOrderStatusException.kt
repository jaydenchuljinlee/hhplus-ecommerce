package com.hhplus.ecommerce.order.infrastructure.exception

class InvalidOrderStatusException(message: String = "유효하지 않은 주문 상태 전이입니다.") : OrderRepositoryException(message)
