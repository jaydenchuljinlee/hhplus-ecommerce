package com.hhplus.ecommerce.common.exception.product

class OutOfStockException(message: String = "상품 재고가 부족합니다."): ProductException(message)