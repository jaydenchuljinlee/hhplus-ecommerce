package com.hhplus.ecommerce.coupon.infrastructure.exception

class CouponAlreadyIssuedException(message: String = "이미 발급된 쿠폰입니다.") : RuntimeException(message)
