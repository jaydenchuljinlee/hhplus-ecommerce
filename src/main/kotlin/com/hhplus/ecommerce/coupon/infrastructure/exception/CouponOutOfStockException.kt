package com.hhplus.ecommerce.coupon.infrastructure.exception

class CouponOutOfStockException(message: String = "쿠폰 재고가 소진되었습니다.") : RuntimeException(message)
