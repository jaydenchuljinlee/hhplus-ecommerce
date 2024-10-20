package com.hhplus.ecommerce.infrastructure.user.exception

import com.hhplus.ecommerce.common.exception.user.UserException

class UserNotFoundException(message: String = "사용자 정보가 존재하지 않습니다."): UserException(message)