package com.hhplus.ecommerce.coupon.infrastructure.exception

import com.hhplus.ecommerce.common.exception.NotFoundException

class UserCouponNotFoundException(message: String = "사용자 쿠폰을 찾을 수 없습니다.") : NotFoundException(message)
