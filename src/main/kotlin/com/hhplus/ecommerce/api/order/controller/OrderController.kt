package com.hhplus.ecommerce.api.order.controller

import com.hhplus.ecommerce.api.CustomApiResponse
import com.hhplus.ecommerce.api.order.dto.OrderCreationRequest
import com.hhplus.ecommerce.api.order.dto.OrderResponse
import com.hhplus.ecommerce.common.exception.product.OutOfStockException
import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("order")
@RestController
class OrderController {

    @Operation(summary = "주문", description = "주문 API")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "주문 성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = CustomApiResponse::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(mediaType = "application/json")])
    ])
    @PostMapping()
    fun prepareOrder(
        @RequestBody request: OrderCreationRequest): CustomApiResponse<OrderResponse> {

        // 상품 정보 Not Found
        if (request.productId == 1L) throw ProductNotFoundException()

        // 재고 부족 Exception
        if (request.productId == 2L) throw OutOfStockException()

        return CustomApiResponse.success(OrderResponse.getInstance())
    }
}