package com.hhplus.ecommerce.domain.product

import com.hhplus.ecommerce.common.config.RedisTestContainerConfig
import com.hhplus.ecommerce.domain.order.OrderService
import com.hhplus.ecommerce.domain.order.dto.OrderCreationCommand
import com.hhplus.ecommerce.domain.payment.PaymentService
import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest
@Import(RedisTestContainerConfig::class)
class ProductServiceTest {
    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun before() {
        (1..7).forEach {
            val order_command = OrderCreationCommand(
                userId = 1,
                productId = it.toLong(),
                quantity = 1,
                price = 100
            )

            orderService.order(order_command)

            val payment_command = CreationPaymentCommand(
                orderId = it.toLong(),
                userId = 1,
                price = 100
            )

            paymentService.pay(payment_command)
        }
    }

    @DisplayName("베스트 주문 Top 5 조회")
    @Test
    fun getBestSellingTop5() {
        val list = productService.getTopFiveLastThreeDaysFromCache()

        assertEquals(list.size, 5)
    }
}