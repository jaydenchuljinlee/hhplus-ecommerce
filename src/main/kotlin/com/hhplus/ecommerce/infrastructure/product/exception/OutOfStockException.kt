package com.hhplus.ecommerce.infrastructure.product.exception

class OutOfStockException(message: String = "상품 재고가 부족합니다."): ProductRepositoryException(message)