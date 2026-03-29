package com.hhplus.ecommerce.payment.infrastructure.jpa.entity

/**
 * 결제 Saga 상태
 *
 * STARTED              : Saga 시작 (결제 요청 접수)
 * BALANCE_DEDUCTED     : 잔액 차감 완료
 * PAYMENT_CREATED      : 결제 엔티티 생성 완료
 * ORDER_CONFIRMED      : 주문 확정 완료
 * STOCK_COMMITTED      : 예약 재고 확정 완료 → COMPLETED 직전
 * COMPLETED            : 전체 결제 프로세스 성공 완료
 * COMPENSATING         : 보상 트랜잭션 진행 중 (환불 시도)
 * COMPENSATION_FAILED  : 보상 트랜잭션 실패 → 수동 처리 필요
 * FAILED               : 결제 실패 (보상 트랜잭션 완료)
 */
enum class PaymentSagaStatus {
    STARTED,
    BALANCE_DEDUCTED,
    PAYMENT_CREATED,
    ORDER_CONFIRMED,
    STOCK_COMMITTED,
    COMPLETED,
    COMPENSATING,
    COMPENSATION_FAILED,
    FAILED
}
