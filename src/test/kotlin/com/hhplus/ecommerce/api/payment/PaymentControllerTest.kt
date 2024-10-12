package com.hhplus.ecommerce.api.payment

import com.hhplus.ecommerce.api.payment.controller.PaymentController
import com.hhplus.ecommerce.api.payment.dto.PaymentRequest
import com.hhplus.ecommerce.common.exception.payment.ExternalPaymentException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PaymentControllerTest {
    private lateinit var paymentController: PaymentController

    @BeforeEach
    fun beforeEach() {
        paymentController = PaymentController()
    }

    @DisplayName("success: 결제 API")
    @Test
    fun successPayment() {
        val request = PaymentRequest.Payment(0L)
        val response = paymentController.payment(request).data!!

        assertEquals(response.id, 0L)
        assertEquals(response.userId, 0L)
        assertEquals(response.orderId, 0L)
        assertEquals(response.productId, 0L)
        assertEquals(response.quantity, 0)
        assertEquals(response.price, 0)
        assertEquals(response.status, "PAYMENT_COMPLETED")
    }

    @DisplayName("외부 PG 결제 연동에서 오류가 발생하면, ExternalPaymentException이 발생한다.")
    @Test
    fun externalPaymentExceptionTest() {
        val request = PaymentRequest.Payment(1L)

        val exception = assertThrows<ExternalPaymentException> {
            paymentController.payment(request)
        }

        assertEquals(exception.message, "외부 PG사 결제에서 오류가 발생했습니다.")
    }
}