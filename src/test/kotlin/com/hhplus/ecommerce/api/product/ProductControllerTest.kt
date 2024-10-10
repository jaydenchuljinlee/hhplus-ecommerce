package com.hhplus.ecommerce.api.product

import com.hhplus.ecommerce.api.product.controller.ProductController
import com.hhplus.ecommerce.api.product.dto.ProductRequest
import com.hhplus.ecommerce.api.product.dto.ProductResponse
import com.hhplus.ecommerce.common.exception.product.ProductNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ProductControllerTest {
    private lateinit var productController: ProductController

    @BeforeEach
    fun before() {
        productController = ProductController()
    }

    @DisplayName("success: 상품 조회 API")
    @Test
    fun successGetProduct() {
        val request = ProductRequest.View(0L)

        val result= productController.getProduct(request)

        val fakeResponse = ProductResponse.Detail.getInstance()

        assertEquals(result.productId, fakeResponse.productId)
        assertEquals(result.name, fakeResponse.name)
        assertEquals(result.price, fakeResponse.price)
        assertEquals(result.imageUrl, fakeResponse.imageUrl)
        assertEquals(result.quantity, fakeResponse.quantity)
        assertEquals(result.description, fakeResponse.description)
    }

    @DisplayName("success: 상품 Top 5 조회 API")
    @Test
    fun successGetTopFiveProduct() {
        val result= productController.getProductTopFive()

        val fakeResponse = ProductResponse.TopFiveResult.getInstance().products[0]

        assertEquals(result.products.size, 5)
        assertEquals(result.products[0].productId, fakeResponse.productId)
        assertEquals(result.products[0].name, fakeResponse.name)
        assertEquals(result.products[0].price, fakeResponse.price)
        assertEquals(result.products[0].imageUrl, fakeResponse.imageUrl)
        assertEquals(result.products[0].quantity, fakeResponse.quantity)
        assertEquals(result.products[0].description, fakeResponse.description)
    }

    @DisplayName("상품 정보가 없으면, ProductNotfoundException이 발생한다.")
    @Test
    fun testProductNotFoundException() {
        val request = ProductRequest.View(1L)

        val exception = assertThrows<ProductNotFoundException> {
            productController.getProduct(request)
        }

        assertEquals(exception.message, "상품 정보가 존재하지 않습니다.")
    }

}