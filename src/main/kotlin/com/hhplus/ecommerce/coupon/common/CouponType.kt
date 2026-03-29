package com.hhplus.ecommerce.coupon.common

/**
 * 쿠폰 할인 타입
 *
 * FIXED        : 정액 할인 (discountValue 원 할인)
 * RATE         : 정률 할인 (discountValue % 할인, maxDiscountAmount 상한 적용)
 * FREE_SHIPPING: 무료 배송
 */
enum class CouponType {
    FIXED,
    RATE,
    FREE_SHIPPING
}
