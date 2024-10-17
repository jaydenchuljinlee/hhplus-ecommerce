package com.hhplus.ecommerce.common.exception.order

class OrderNotFoundException(message: String = "주문정보를 조회할 수 없습니다."): OrderException(message) {
}