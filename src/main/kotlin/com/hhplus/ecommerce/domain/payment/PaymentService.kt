package com.hhplus.ecommerce.domain.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.PaymentKafkaProperties
import com.hhplus.ecommerce.domain.payment.dto.CreationPaymentCommand
import com.hhplus.ecommerce.domain.payment.dto.PaymentResult
import com.hhplus.ecommerce.domain.payment.repository.IPaymentRepository
import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.infrastructure.payment.mongodb.PaymentHistoryDocument
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PaymentService(
    private val paymentRepository: IPaymentRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val paymentKafkaProperties: PaymentKafkaProperties,
    private val objectMapper: ObjectMapper,
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

        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = paymentKafkaProperties.groupId,
            topic = paymentKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(paymentHistoryDocument)
        )

        applicationEventPublisher.publishEvent(outboxEvent)

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