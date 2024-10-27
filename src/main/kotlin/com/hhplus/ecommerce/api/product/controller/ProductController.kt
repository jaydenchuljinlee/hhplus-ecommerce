package com.hhplus.ecommerce.api.product.controller

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.api.product.dto.*
import com.hhplus.ecommerce.domain.product.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
): IProductController {
    override fun getProduct(request: ProductInfoQueryRequest): CustomApiResponse<ProductInfoResponse> {
        val result = productService.getProduct(request.toProductInfoQuery())

        return CustomApiResponse.success(ProductInfoResponse.from(result))
    }

    override fun getProductTopFive(): CustomApiResponse<List<BestSellingTopFiveResponse>> {
        val result = productService.getTopFiveLastThreeDays()
        val response = result.map { BestSellingTopFiveResponse.from(it) }
        return CustomApiResponse.success(response)
    }
}