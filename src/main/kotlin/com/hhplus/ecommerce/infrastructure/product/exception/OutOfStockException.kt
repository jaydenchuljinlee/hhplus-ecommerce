package com.hhplus.ecommerce.infrastructure.product.exception

import com.hhplus.ecommerce.common.exception.product.ProductException

class OutOfStockException(message: String = "상품 재고가 부족합니다."): ProductException(message)