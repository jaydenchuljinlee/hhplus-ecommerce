package com.hhplus.ecommerce.infrastructure.cart.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class CartRepositoryException(message: String): RepositoryException(message)