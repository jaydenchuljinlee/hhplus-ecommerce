package com.hhplus.ecommerce.payment.common

enum class PayMethod {
    CREDIT_CARD,    // 신용/체크카드 단독
    BANK_TRANSFER,  // 계좌이체 단독
    BALANCE,        // 잔액 단독 (서비스 포인트/머니)
    POINT,          // 포인트 단독
    MIXED           // 복합 결제 (잔액 + 카드 등 2가지 이상 수단 혼합)
}