package com.hhplus.ecommerce.coupon.infrastructure.exception

import com.hhplus.ecommerce.common.exception.ServiceException

class CouponSoldOutException(message: String = "쿠폰 수량이 소진되었습니다.") : ServiceException(message)
