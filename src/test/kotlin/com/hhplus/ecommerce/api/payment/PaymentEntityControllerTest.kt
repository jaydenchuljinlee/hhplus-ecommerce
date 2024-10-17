package com.hhplus.ecommerce.api.payment

import com.hhplus.ecommerce.api.payment.controller.PaymentController
import com.hhplus.ecommerce.api.payment.dto.PaymentCreationRequest
import com.hhplus.ecommerce.usercase.payment.PaymentFacade
import com.hhplus.ecommerce.usercase.payment.dto.PaymentCreation
import com.hhplus.ecommerce.usercase.payment.dto.PaymentInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PaymentEntityControllerTest {
    @Mock
    private lateinit var paymentFacade: PaymentFacade

    private lateinit var paymentController: PaymentController

    @BeforeEach
    fun beforeEach() {
        paymentController = PaymentController(paymentFacade)
    }

    @DisplayName("success: 결제 API")
    @Test
    fun successPayment() {
        val param = PaymentCreation(0L, 0L)

        val result = PaymentInfo(
            paymentId = 0L,
            userId = 0L,
            orderId = 0L,
            price = 2000,
            status = "PAYMENT_COMPLETED"
        )

        BDDMockito.given(paymentFacade.pay(param)).willReturn(result)

        val request = PaymentCreationRequest(0L, 0L)
        val response = paymentController.payment(request).data!!

        assertEquals(response.id, 0L)
        assertEquals(response.userId, 0L)
        assertEquals(response.orderId, 0L)
        assertEquals(response.price, 2000)
        assertEquals(response.status, "PAYMENT_COMPLETED")
    }
//
//    @DisplayName("외부 PG 결제 연동에서 오류가 발생하면, ExternalPaymentException이 발생한다.")
//    @Test
//    fun externalPaymentExceptionTest() {
//        val request = PaymentCreationRequest(0, 0)
//
//        val exception = assertThrows<ExternalPaymentException> {
//            paymentController.payment(request)
//        }
//
//        assertEquals(exception.message, "외부 PG사 결제에서 오류가 발생했습니다.")
//    }
}