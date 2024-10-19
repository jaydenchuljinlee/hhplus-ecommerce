package com.hhplus.ecommerce.common.exception.payment

class PaymentExternalException(message: String = "외부 결제 연동에서 오류가 발생했습니다."): ExternalException(message) {
}