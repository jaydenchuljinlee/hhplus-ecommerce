package com.hhplus.ecommerce.domain.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.domain.payment.dto.PaymentResult
import com.hhplus.ecommerce.domain.payment.repository.IPaymentRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.event.OutboxEventProducer
import com.hhplus.ecommerce.infrastructure.outboxevent.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.infrastructure.payment.event.PaymentEventPublisher
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PaymentService(
    private val paymentRepository: IPaymentRepository,
    private val outboxEventProducer: OutboxEventProducer
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

        val outboxEventEntity = OutboxEventEntity(
            id = UUID.randomUUID(),
            groupId = "PAYMENT_HISTORY_GROUP",
            topic = "PAYMENT_HISTORY",
            payload = ObjectMapper().writeValueAsString(paymentHistoryDocument)
        )

        outboxEventProducer.afterCommit(outboxEventEntity)

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