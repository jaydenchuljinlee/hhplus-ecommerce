package com.hhplus.ecommerce.infrastructure.order.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class OrderRepositoryException(message: String): RepositoryException(message) {
}