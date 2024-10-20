package com.hhplus.ecommerce.infrastructure.order.exception

import com.hhplus.ecommerce.common.exception.order.OrderException

class OrderNotFoundException(message: String = "주문정보를 조회할 수 없습니다."): OrderException(message) {
}