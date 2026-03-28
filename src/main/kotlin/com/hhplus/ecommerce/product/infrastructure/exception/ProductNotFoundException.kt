package com.hhplus.ecommerce.product.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class ProductNotFoundException(message: String = "상품 정보가 존재하지 않습니다."): NotFoundException(message)