package com.hhplus.ecommerce.coupon.common

/**
 * 사용자 쿠폰 상태
 *
 * ISSUED  : 발급 완료, 사용 가능
 * USED    : 사용 완료
 * EXPIRED : 유효기간 만료
 */
enum class UserCouponStatus {
    ISSUED,
    USED,
    EXPIRED
}
