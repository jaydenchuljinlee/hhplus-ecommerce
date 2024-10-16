package com.hhplus.ecommerce.api.payment.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.payment.dto.PaymentCreationRequest
import com.hhplus.ecommerce.api.payment.dto.PaymentResponse
import com.hhplus.ecommerce.common.exception.payment.ExternalPaymentException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/payment")
@RestController
class PaymentController {

    @Operation(summary = "결제", description = "결제 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "결제 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @PostMapping()
    fun payment(
        @RequestBody request: PaymentCreationRequest
    ): CustomApiResponse<PaymentResponse> {

        if (request.orderId == 1L) throw ExternalPaymentException()

        return CustomApiResponse.success(PaymentResponse.getInstance())
    }
}