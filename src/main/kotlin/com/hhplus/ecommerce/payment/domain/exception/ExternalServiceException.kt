package com.hhplus.ecommerce.payment.domain.exception

import com.hhplus.ecommerce.common.exception.ServiceException

abstract class ExternalServiceException(message: String): ServiceException(message)