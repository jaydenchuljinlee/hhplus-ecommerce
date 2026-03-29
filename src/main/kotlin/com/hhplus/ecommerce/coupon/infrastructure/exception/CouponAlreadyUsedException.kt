package com.hhplus.ecommerce.coupon.infrastructure.exception

import com.hhplus.ecommerce.common.exception.ServiceException

class CouponAlreadyUsedException(message: String = "이미 사용된 쿠폰입니다.") : ServiceException(message)
