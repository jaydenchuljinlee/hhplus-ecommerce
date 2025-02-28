package com.hhplus.ecommerce.product.infrastructure.exception

class ProductNotFoundException(message: String = "상품 정보가 존재하지 않습니다."): ProductRepositoryException(message)