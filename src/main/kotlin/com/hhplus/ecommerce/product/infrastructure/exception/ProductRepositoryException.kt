package com.hhplus.ecommerce.product.infrastructure.exception

import com.hhplus.ecommerce.common.exception.RepositoryException

abstract class ProductRepositoryException(message: String): RepositoryException(message)