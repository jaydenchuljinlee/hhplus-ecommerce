package com.hhplus.ecommerce.domain.payment

import com.hhplus.ecommerce.common.exception.payment.ExternalPaymentException
import com.hhplus.ecommerce.domain.payment.dto.ExternalCallRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ExternalApiService {
    private val logger = LoggerFactory.getLogger(ExternalApiService::class.java)

    fun call(request: ExternalCallRequest) {
        try {
            throw ExternalPaymentException()
        } catch (e: ExternalPaymentException) {
            logger.error("EXTERNAL:PAYMENT:ERROR:{} -> {}", request.payId, request)
        }
    }
}