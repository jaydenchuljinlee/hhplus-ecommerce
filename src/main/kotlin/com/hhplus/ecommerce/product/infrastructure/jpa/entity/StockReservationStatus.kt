package com.hhplus.ecommerce.product.infrastructure.jpa.entity

/**
 * 예약 재고 상태
 *
 * RESERVED  : 주문 생성 시 재고 임시 점유 (soft reserve)
 * COMMITTED : 결제 완료 후 실제 차감 확정
 * RELEASED  : 결제 취소 / 실패로 예약 해제 (재고 복구)
 * EXPIRED   : TTL 초과로 자동 만료 해제
 */
enum class StockReservationStatus {
    RESERVED,
    COMMITTED,
    RELEASED,
    EXPIRED
}
