package com.hhplus.ecommerce.user.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class UserNotFoundException(message: String = "사용자 정보가 존재하지 않습니다."): NotFoundException(message)