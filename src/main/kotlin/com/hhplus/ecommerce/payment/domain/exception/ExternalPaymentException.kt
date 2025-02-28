package com.hhplus.ecommerce.payment.domain.exception

class ExternalPaymentException(message: String = "외부 PG사 결제에서 오류가 발생했습니다."): ExternalServiceException(message)