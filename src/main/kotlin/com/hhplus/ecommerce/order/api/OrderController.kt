package com.hhplus.ecommerce.order.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.order.api.dto.OrderCreationRequest
import com.hhplus.ecommerce.order.api.dto.OrderResponse
import com.hhplus.ecommerce.order.usecase.OrderFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    private val orderFacade: OrderFacade
) : IOrderController {

    override fun prepareOrder(request: OrderCreationRequest): ResponseEntity<CustomApiResponse<OrderResponse>> {
        val result = orderFacade.order(request.toOrderCreation())
        return ResponseEntity.status(201).body(CustomApiResponse.created(OrderResponse.from(result)))
    }
}
