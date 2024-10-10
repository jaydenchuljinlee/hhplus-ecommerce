package com.hhplus.ecommerce.api.product.controller

import com.hhplus.ecommerce.api.ApiResponse
import com.hhplus.ecommerce.api.product.dto.ProductRequest
import com.hhplus.ecommerce.api.product.dto.ProductResponse
import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/product")
@RestController
class ProductController {
    @GetMapping()
    fun getProduct(@RequestBody request: ProductRequest.View): ApiResponse<ProductResponse.Detail> {

        if (request.productId == 1L) throw ProductNotFoundException()

        return ApiResponse.success(ProductResponse.Detail.getInstance())
    }

    @GetMapping("list/top_five")
    fun getProductTopFive(): ApiResponse<ProductResponse.TopFiveResult> {
        return ApiResponse.success(ProductResponse.TopFiveResult.getInstance())
    }
}