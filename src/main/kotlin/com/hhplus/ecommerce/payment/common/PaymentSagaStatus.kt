package com.hhplus.ecommerce.payment.common

enum class PaymentSagaStatus {
    STARTED,
    BALANCE_DEDUCTED,
    PAYMENT_CREATED,
    ORDER_CONFIRMED,
    STOCK_COMMITTED,
    COMPENSATING,
    COMPENSATION_FAILED,
    COMPLETED,
    FAILED
}
