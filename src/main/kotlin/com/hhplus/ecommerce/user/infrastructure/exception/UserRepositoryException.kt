package com.hhplus.ecommerce.user.infrastructure.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class UserRepositoryException(message: String): RepositoryException(message)