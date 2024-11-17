package com.hhplus.ecommerce.domain.payment

import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.domain.payment.dto.ExternalCallRequest
import com.hhplus.ecommerce.domain.payment.dto.PaymentResult
import com.hhplus.ecommerce.domain.payment.repository.IPaymentHistoryRepository
import com.hhplus.ecommerce.domain.payment.repository.IPaymentRepository
import com.hhplus.ecommerce.infrastructure.payment.event.PaymentEventPublisher
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentHistoryEntity
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryMongoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: IPaymentRepository,
    private val paymentEventPublisher: PaymentEventPublisher,
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
        paymentEventPublisher.publish(paymentHistoryDocument)

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