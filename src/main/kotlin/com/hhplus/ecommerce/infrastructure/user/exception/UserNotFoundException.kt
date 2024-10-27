package com.hhplus.ecommerce.infrastructure.user.exception

class UserNotFoundException(message: String = "사용자 정보가 존재하지 않습니다."): UserRepositoryException(message)