package com.hhplus.ecommerce.api.order.controller

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
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


@RestController
class OrderController(
    private val orderFacade: OrderFacade
): IOrderController {
    override fun prepareOrder(request: OrderCreationRequest): CustomApiResponse<OrderResponse> {

        val result = orderFacade.order(request.toOrderCreation())

        return CustomApiResponse.success(OrderResponse.from(result))
    }
}