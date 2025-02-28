package com.hhplus.ecommerce.product.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.product.domain.ProductService
import com.hhplus.ecommerce.product.api.dto.BestSellingTopFiveResponse
import com.hhplus.ecommerce.product.api.dto.ProductInfoQueryRequest
import com.hhplus.ecommerce.product.api.dto.ProductInfoResponse
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
): IProductController {
    override fun getProductFromCache(request: ProductInfoQueryRequest): CustomApiResponse<ProductInfoResponse> {
        val result = productService.getProductCache(request.toProductInfoQuery())
        return CustomApiResponse.success(ProductInfoResponse.from(result))
    }

    override fun getProductFromDB(request: ProductInfoQueryRequest): CustomApiResponse<ProductInfoResponse> {
        val result = productService.getProductDB(request.toProductInfoQuery())
        return CustomApiResponse.success(ProductInfoResponse.from(result))
    }

    override fun getTopFiveLastThreeDaysFromCache(): CustomApiResponse<List<BestSellingTopFiveResponse>> {
        val result = productService.getTopFiveLastThreeDaysFromCache()
        val response = result.map { BestSellingTopFiveResponse.from(it) }
        return CustomApiResponse.success(response)
    }
    override fun getTopFiveLastThreeDaysFromDB(): CustomApiResponse<List<BestSellingTopFiveResponse>> {
        val result = productService.getTopFiveLastThreeDaysFromDB()
        val response = result.map { BestSellingTopFiveResponse.from(it) }
        return CustomApiResponse.success(response)
    }
}