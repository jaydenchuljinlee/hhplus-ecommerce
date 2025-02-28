package com.hhplus.ecommerce.order.infrastructure.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class OrderRepositoryException(message: String): RepositoryException(message) {
}