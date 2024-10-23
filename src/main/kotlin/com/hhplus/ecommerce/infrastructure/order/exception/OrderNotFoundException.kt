package com.hhplus.ecommerce.infrastructure.order.exception

class OrderNotFoundException(message: String = "주문정보를 조회할 수 없습니다."): OrderRepositoryException(message) {
}