package com.hhplus.ecommerce.infrastructure.cart.exception

class DuplicatedProductException(message: String = "같은 상품이 장바구니에 존재합니다."): CartRepositoryException(message)