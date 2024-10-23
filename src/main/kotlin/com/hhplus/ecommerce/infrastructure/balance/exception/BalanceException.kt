package com.hhplus.ecommerce.infrastructure.balance.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class BalanceException(message: String): RepositoryException(message)