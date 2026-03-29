package com.hhplus.ecommerce.coupon.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class CouponNotFoundException(message: String = "쿠폰 정책을 찾을 수 없습니다.") : NotFoundException(message)
