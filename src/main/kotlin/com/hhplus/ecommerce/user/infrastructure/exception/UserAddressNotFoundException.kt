package com.hhplus.ecommerce.user.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class UserAddressNotFoundException(message: String = "배송지를 찾을 수 없습니다.") : NotFoundException(message)
