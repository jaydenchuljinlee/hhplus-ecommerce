package com.hhplus.ecommerce.payment.domain.exception

import com.hhplus.ecommerce.common.exception.ServiceException

abstract class PaymentServiceException(message: String): ServiceException(message)