package com.hhplus.ecommerce.api.payment.controller

import com.hhplus.ecommerce.api.payment.dto.PaymentRequest
import com.hhplus.ecommerce.api.payment.dto.PaymentResponse
import com.hhplus.ecommerce.common.exception.payment.ExternalPaymentException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/payment")
@RestController
class PaymentController {
    @PostMapping()
    fun payment(request: PaymentRequest.Payment): PaymentResponse.Payment {

        if (request.orderId == 1L) throw ExternalPaymentException()

        return PaymentResponse.Payment.getInstance()
    }
}