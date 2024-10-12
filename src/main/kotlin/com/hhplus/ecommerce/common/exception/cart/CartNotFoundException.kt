package com.hhplus.ecommerce.common.exception.cart

class CartNotFoundException(message: String = "존재하지 않는 장바구니입니다."): CartException(message) {
}