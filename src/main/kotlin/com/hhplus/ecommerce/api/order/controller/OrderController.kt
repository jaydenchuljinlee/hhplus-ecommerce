package com.hhplus.ecommerce.api.order.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.CustomErrorResponse
import com.hhplus.ecommerce.api.order.dto.OrderCreationRequest
import com.hhplus.ecommerce.api.order.dto.OrderResponse
import com.hhplus.ecommerce.usercase.order.OrderFacade
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

@RequestMapping("order")
@RestController
class OrderController(
    private val orderFacade: OrderFacade
) {

    @Tag(name = "주문 기능")
    @Operation(summary = "주문 API", description = "주문 요청을 처리하는 API입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "주문 성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류", content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping()
    fun prepareOrder(
        @RequestBody request: OrderCreationRequest): CustomApiResponse<OrderResponse> {

        val result = orderFacade.order(request.toOrderCreation())

        return CustomApiResponse.success(OrderResponse.from(result))
    }
}