package com.hhplus.ecommerce.api.payment.controller

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.api.payment.dto.PaymentCreationRequest
import com.hhplus.ecommerce.api.payment.dto.PaymentResponse
import com.hhplus.ecommerce.usercase.payment.PaymentFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
class PaymentController(
    private val paymentFacade: PaymentFacade
): IPaymentController {
    override fun payment(request: PaymentCreationRequest): CustomApiResponse<PaymentResponse> {

        val result = paymentFacade.pay(request.toPaymentCreation())

        return CustomApiResponse.success(PaymentResponse.from(result))
    }
}