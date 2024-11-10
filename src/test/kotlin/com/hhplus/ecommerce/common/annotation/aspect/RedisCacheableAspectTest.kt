package com.hhplus.ecommerce.common.annotation.aspect

import com.hhplus.ecommerce.common.config.RedisTestContainerConfig
import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.domain.product.ProductService
import com.hhplus.ecommerce.domain.product.dto.ProductInfoQuery
import com.hhplus.ecommerce.domain.product.dto.ProductInfoResult
import com.hhplus.ecommerce.infrastructure.product.ProductRepository
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductDetailEntity
import com.hhplus.ecommerce.infrastructure.product.jpa.entity.ProductEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.redisson.api.RedissonClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ActiveProfiles("test")
@SpringBootTest
@Import(RedisTestContainerConfig::class)
class RedisCacheableAspectTest {
    @Autowired
    private lateinit var productService: ProductService

    @MockBean
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @DisplayName("RedisCacheable 어노테이션이 붙은 메서드는 캐싱이 적용되어, 초기 한 번만 호출된다.")
    @Test
    fun testRedisCacheable() {
        val queryDto = ProductInfoQuery(productId = 1)

        val productEntity = ProductEntity(
            id = 1,
            name = "testProduct",
            price = 10000,
            activeYn = StateYn.Y
        )

        val productDetailEntity = ProductDetailEntity(
            id = 1,
            productId = 1,
            productOptionId = 1,
            quantity = 10
        )

        `when`(productRepository.findByProductId(queryDto.productId)).thenReturn(productDetailEntity)
        `when`(productRepository.findById(queryDto.productId)).thenReturn(productEntity)

        val expectedResult = ProductInfoResult(
            productId = productEntity.id,
            productName = productEntity.name,
            price = productEntity.price,
            stock = productDetailEntity.quantity
        )

        // 캐시가 없는 상태에서 Mock Repository를 조회하는지 테스트
        val firstResult = productService.getProductCache(queryDto)
        assertEquals(expectedResult, firstResult)

        // 두 번째 조회에서는 캐시에서 가져옴
        val cachedResult = productService.getProductCache(queryDto)
        assertEquals(expectedResult, cachedResult)

        // 정확히 1번 DB를 호출했는지 테스트
        verify(productRepository, times(1)).findByProductId(1L)
        verify(productRepository, times(1)).findById(1L)

        // 캐시가 적용 된 상태에서 캐시에서 조회하는지 테스트
        val cache = redissonClient.getBucket<ProductInfoResult>("product:cache:1", TypedJsonJacksonCodec(ProductInfoResult::class.java))
        assertTrue(cache != null)

        val cacheData = cache.get()
        assertEquals(expectedResult, cacheData)
    }
}