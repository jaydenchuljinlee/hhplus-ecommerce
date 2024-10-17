package com.hhplus.ecommerce.api.product

import com.hhplus.ecommerce.api.product.controller.ProductController
import com.hhplus.ecommerce.api.product.dto.ProductInfoQueryRequest
import com.hhplus.ecommerce.domain.product.ProductService
import com.hhplus.ecommerce.domain.product.dto.ProductInfoQuery
import com.hhplus.ecommerce.domain.product.dto.ProductInfoResult
import com.hhplus.ecommerce.infrastructure.product.dto.BestSellingProduct
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ProductControllerTest {
    @Mock
    private lateinit var productService: ProductService

    private lateinit var productController: ProductController

    @BeforeEach
    fun before() {
        productController = ProductController(productService)
    }

    @DisplayName("success: 상품 조회 API")
    @Test
    fun successGetProduct() {
        // Given
        val param = ProductInfoQuery(0)

        val result = ProductInfoResult(
            productId = 0,
            productName = "name",
            price = 1000,
            stock = 100,
        )
        BDDMockito.given(productService.getProduct(param)).willReturn(result)

        val request = ProductInfoQueryRequest(0)
        val response = productController.getProduct(request).data!!

        assertEquals(response.productId, result.productId)
        assertEquals(response.productName, result.productName)
        assertEquals(response.price, result.price)
        assertEquals(response.quantity, result.stock)
    }

    @DisplayName("success: 상품 Top 5 조회 API")
    @Test
    fun successGetTopFiveProduct() {
        // Given
        val list = mutableListOf<BestSellingProduct>()

        (1..5).forEach {
            val result = BestSellingProduct(
                productId = it.toLong(),
                productName = "",
                stock = 10,
                totalOrderCount = 100,
                totalPayPrice = 1000,
                orderCount = 10,
                payCount = 10,
            )
            list.add(result)
        }


        BDDMockito.given(productService.getTopFiveLastThreeDays()).willReturn(list)

        val response = productController.getProductTopFive().data!!

        assertEquals(response.size, 5)
        assertEquals(response[0].productId, list[0].productId)
        assertEquals(response[0].productName, list[0].productName)
        assertEquals(response[0].stock, list[0].stock)
        assertEquals(response[0].totalOrderCount, list[0].totalOrderCount)
        assertEquals(response[0].totalPayPrice, list[0].totalPayPrice)
        assertEquals(response[0].orderCount, list[0].orderCount)
        assertEquals(response[0].payCount, list[0].payCount)
    }

//    @DisplayName("상품 정보가 없으면, ProductNotfoundException이 발생한다.")
//    @Test
//    fun testProductNotFoundException() {
//        val request = ProductInfoQueryRequest(0)
//
//        val exception = assertThrows<ProductNotFoundException> {
//            productController.getProduct(request)
//        }
//
//        assertEquals(exception.message, "상품 정보가 존재하지 않습니다.")
//    }

}