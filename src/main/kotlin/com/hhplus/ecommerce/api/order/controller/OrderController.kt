package com.hhplus.ecommerce.api.order.controller

import com.hhplus.ecommerce.api.ApiResponse
import com.hhplus.ecommerce.api.order.dto.OrderRequest
import com.hhplus.ecommerce.api.order.dto.OrderResponse
import com.hhplus.ecommerce.common.exception.product.OutOfStockException
import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("order")
@RestController
class OrderController {
    @PostMapping("preparation")
    fun prepareOrder(request: OrderRequest.Preparation): ApiResponse<OrderResponse.Preparation> {

        // 상품 정보 Not Found
        if (request.productId == 1L) throw ProductNotFoundException()

        // 재고 부족 Exception
        if (request.productId == 2L) throw OutOfStockException()

        return ApiResponse.success(OrderResponse.Preparation.getInstance())
    }
}