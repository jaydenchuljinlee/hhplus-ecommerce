package com.hhplus.ecommerce.domain.payment.exception

class PaymentExternalException(message: String = "외부 결제 연동에서 오류가 발생했습니다."): PaymentServiceException(message) {
}