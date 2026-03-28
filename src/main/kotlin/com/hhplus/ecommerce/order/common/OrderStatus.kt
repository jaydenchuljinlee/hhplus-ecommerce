package com.hhplus.ecommerce.order.common

enum class OrderStatus {
    REQUESTED,       // 주문 생성됨 — 재고 차감 이벤트 발행 대기
    STOCK_CONFIRMED, // 재고 확보 완료 — 결제 진행 가능
    STOCK_FAILED,    // 재고 확보 실패 — 결제 불가, 주문 만료
    CONFIRMED,       // 결제 완료 — 주문 최종 확정
    CANCELED         // 주문 취소
}