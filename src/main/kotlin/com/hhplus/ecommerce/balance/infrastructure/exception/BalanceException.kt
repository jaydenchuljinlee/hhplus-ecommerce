package com.hhplus.ecommerce.balance.infrastructure.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class BalanceException(message: String): RepositoryException(message)