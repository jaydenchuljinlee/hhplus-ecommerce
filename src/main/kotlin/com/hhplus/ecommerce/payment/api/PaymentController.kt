package com.hhplus.ecommerce.payment.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.payment.api.dto.PaymentCreationRequest
import com.hhplus.ecommerce.payment.api.dto.PaymentResponse
import com.hhplus.ecommerce.payment.usecase.PaymentFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(
    private val paymentFacade: PaymentFacade
) : IPaymentController {

    override fun payment(request: PaymentCreationRequest): ResponseEntity<CustomApiResponse<PaymentResponse>> {
        val result = paymentFacade.pay(request.toPaymentCreation())
        return ResponseEntity.status(201).body(CustomApiResponse.created(PaymentResponse.from(result)))
    }
}
