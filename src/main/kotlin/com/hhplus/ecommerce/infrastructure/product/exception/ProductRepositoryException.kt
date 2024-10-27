package com.hhplus.ecommerce.infrastructure.product.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class ProductRepositoryException(message: String): RepositoryException(message)