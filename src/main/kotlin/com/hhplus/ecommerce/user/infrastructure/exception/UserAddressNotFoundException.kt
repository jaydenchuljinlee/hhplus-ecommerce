package com.hhplus.ecommerce.user.infrastructure.exception

class UserAddressNotFoundException(message: String = "주소를 찾을 수 없습니다.") : RuntimeException(message)
