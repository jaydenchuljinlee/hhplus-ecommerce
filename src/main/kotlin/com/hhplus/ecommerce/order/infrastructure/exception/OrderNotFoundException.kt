package com.hhplus.ecommerce.order.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class OrderNotFoundException(message: String = "주문정보를 조회할 수 없습니다."): NotFoundException(message)