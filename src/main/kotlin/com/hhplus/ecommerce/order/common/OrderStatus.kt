package com.hhplus.ecommerce.order.common

enum class OrderStatus {
    REQUESTED,           // 주문 요청
    CONFIRMED,           // 결제 확인
    PREPARING,           // 상품 준비중
    READY_TO_SHIP,       // 발송 대기
    SHIPPED,             // 발송 완료
    IN_TRANSIT,          // 배송중
    DELIVERED,           // 배송 완료
    PURCHASE_CONFIRMED,  // 구매 확정
    RETURN_REQUESTED,    // 반품 요청
    RETURNED,            // 반품 완료
    REFUND_PENDING,      // 환불 대기
    REFUNDED,            // 환불 완료
    CANCELED             // 주문 취소
}
