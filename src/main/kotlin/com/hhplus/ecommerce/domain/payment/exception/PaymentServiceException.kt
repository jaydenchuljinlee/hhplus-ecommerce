package com.hhplus.ecommerce.domain.payment.exception

import com.hhplus.ecommerce.common.exception.ServiceException

abstract class PaymentServiceException(message: String): ServiceException(message)