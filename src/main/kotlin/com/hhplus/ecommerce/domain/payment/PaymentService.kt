package com.hhplus.ecommerce.domain.payment

import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.domain.payment.dto.PaymentResult
import com.hhplus.ecommerce.domain.payment.repository.IPaymentRepository
import com.hhplus.ecommerce.infrastructure.payment.event.PaymentEventPublisher
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: IPaymentRepository,
    private val paymentSpringEventPublisher: PaymentEventPublisher,
) {
    @Transactional
    fun pay(dto: CreationPaymentCommand): PaymentResult {
        val paymentEntity = PaymentEntity(
            orderId = dto.orderId,
            status = "PAYMENT_COMPLETED",
            price = dto.price
        )

        val entity = paymentRepository.insertOrUpdate(paymentEntity)

        val paymentHistoryDocument = PaymentHistoryDocument(
            paymentId = entity.id,
            userId = dto.userId,
            price = entity.price,
            status = entity.status
        )

        // 이력 저장을 외부 연동으로 이관
        paymentSpringEventPublisher.publish(paymentHistoryDocument)

        val result = PaymentResult(
            paymentId = entity.id,
            userId = dto.userId,
            orderId = entity.orderId,
            status = entity.status,
            price = entity.price,
        )

        return result
    }
}