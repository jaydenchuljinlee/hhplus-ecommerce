package com.hhplus.ecommerce.domain.payment

import com.hhplus.ecommerce.payment.common.PayStatus
import com.hhplus.ecommerce.payment.domain.PaymentService
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.event.IPaymentEventPublisher
import com.hhplus.ecommerce.payment.domain.repository.IPaymentRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.payment.common.PayMethod
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

    @Mock private lateinit var paymentRepository: IPaymentRepository
    @Mock private lateinit var paymentEventPublisher: IPaymentEventPublisher

    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun setup() {
        paymentService = PaymentService(paymentRepository, paymentEventPublisher)
    }

    // ─────────────────────────────────────────────────────
    // pay
    // ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("pay()")
    inner class Pay {

        @Test
        @DisplayName("결제 요청이 들어오면 PAID 상태의 PaymentResult를 반환한다")
        fun `결제 성공 - PAID 상태 반환`() {
            // Given
            val command = CreationPaymentCommand(orderId = 1L, userId = 2L, price = 10_000L)
            val savedEntity = PaymentEntity(
                id = 100L,
                orderId = command.orderId,
                status = PayStatus.PAID,
                payMethod = PayMethod.CREDIT_CARD,
                price = command.price
            )
            given(paymentRepository.insertOrUpdate(org.mockito.ArgumentMatchers.any())).willReturn(savedEntity)

            // When
            val result = paymentService.pay(command)

            // Then
            assertEquals(100L, result.paymentId)
            assertEquals(command.orderId, result.orderId)
            assertEquals(command.userId, result.userId)
            assertEquals(command.price, result.price)
            assertEquals(PayStatus.PAID, result.status)
        }

        @Test
        @DisplayName("결제 성공 시 PaymentEntity가 올바른 값으로 저장된다")
        fun `결제 성공 - 저장 엔티티 검증`() {
            // Given
            val command = CreationPaymentCommand(orderId = 5L, userId = 3L, price = 50_000L)
            val captor = ArgumentCaptor.forClass(PaymentEntity::class.java)
            val savedEntity = PaymentEntity(
                id = 1L,
                orderId = command.orderId,
                status = PayStatus.PAID,
                payMethod = PayMethod.CREDIT_CARD,
                price = command.price
            )
            given(paymentRepository.insertOrUpdate(captor.capture())).willReturn(savedEntity)

            // When
            paymentService.pay(command)

            // Then
            val capturedEntity = captor.value
            assertEquals(command.orderId, capturedEntity.orderId)
            assertEquals(PayStatus.PAID, capturedEntity.status)
            assertEquals(PayMethod.CREDIT_CARD, capturedEntity.payMethod)
            assertEquals(command.price, capturedEntity.price)
        }

        @Test
        @DisplayName("결제 성공 시 PaymentEventPublisher.publishPay가 호출된다")
        fun `결제 성공 시 이벤트 발행`() {
            // Given
            val command = CreationPaymentCommand(orderId = 1L, userId = 2L, price = 10_000L)
            val savedEntity = PaymentEntity(
                id = 100L,
                orderId = command.orderId,
                status = PayStatus.PAID,
                payMethod = PayMethod.CREDIT_CARD,
                price = command.price
            )
            given(paymentRepository.insertOrUpdate(org.mockito.ArgumentMatchers.any())).willReturn(savedEntity)

            // When
            val result = paymentService.pay(command)

            // Then
            verify(paymentEventPublisher).publishPay(result)
        }
    }
}
