package com.hhplus.ecommerce.cart.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class CartNotFoundException(message: String = "존재하지 않는 장바구니입니다."): NotFoundException(message)