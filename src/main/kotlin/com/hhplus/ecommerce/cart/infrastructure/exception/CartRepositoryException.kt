package com.hhplus.ecommerce.cart.infrastructure.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class CartRepositoryException(message: String): RepositoryException(message)