package com.hhplus.ecommerce.notification.common

/**
 * 알림 유형
 */
enum class NotificationType(val title: String) {
    ORDER_PLACED("주문이 접수되었습니다"),
    PAYMENT_CONFIRMED("결제가 완료되었습니다"),
    SHIPPED("상품이 발송되었습니다"),
    DELIVERED("배송이 완료되었습니다"),
    STOCK_ALERT("관심 상품 재입고 알림")
}
