package com.hhplus.ecommerce.order.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.order.api.dto.OrderCreationRequest
import com.hhplus.ecommerce.order.api.dto.OrderResponse
import com.hhplus.ecommerce.order.usecase.OrderFacade
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