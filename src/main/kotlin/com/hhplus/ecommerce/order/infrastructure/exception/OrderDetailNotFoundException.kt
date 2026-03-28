package com.hhplus.ecommerce.order.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class OrderDetailNotFoundException(message: String = "주문 상세 정보를 조회할 수 없습니다.") : NotFoundException(message)
