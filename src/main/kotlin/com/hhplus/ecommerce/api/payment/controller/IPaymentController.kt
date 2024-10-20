package com.hhplus.ecommerce.api.payment.controller

import com.hhplus.ecommerce.api.payment.dto.PaymentCreationRequest
import com.hhplus.ecommerce.api.payment.dto.PaymentResponse
import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/payment")
interface IPaymentController {
    @Tag(name = "결제 기능")
    @Operation(summary = "결제 API", description = "주문한 상품을 결제하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "결제 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping()
    fun payment(
        @RequestBody request: PaymentCreationRequest
    ): CustomApiResponse<PaymentResponse>
}