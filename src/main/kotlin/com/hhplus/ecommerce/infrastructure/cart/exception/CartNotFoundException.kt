package com.hhplus.ecommerce.infrastructure.cart.exception

class CartNotFoundException(message: String = "존재하지 않는 장바구니입니다."): CartRepositoryException(message) {
}