package com.hhplus.ecommerce.infrastructure.user.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class UserRepositoryException(message: String): RepositoryException(message)