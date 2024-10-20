package com.hhplus.ecommerce.domain.payment.exception

import com.hhplus.ecommerce.common.exception.payment.ExternalException

class ExternalPaymentException(message: String = "외부 PG사 결제에서 오류가 발생했습니다."): ExternalException(message)