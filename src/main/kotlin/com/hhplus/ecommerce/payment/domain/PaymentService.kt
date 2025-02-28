package com.hhplus.ecommerce.payment.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.PaymentKafkaProperties
import com.hhplus.ecommerce.payment.domain.dto.CreationPaymentCommand
import com.hhplus.ecommerce.payment.domain.dto.PaymentResult
import com.hhplus.ecommerce.payment.domain.repository.IPaymentRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.payment.common.PayMethod
import com.hhplus.ecommerce.payment.common.PayStatus
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentEntity
import com.hhplus.ecommerce.payment.infrastructure.mongodb.PaymentHistoryDocument
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
            status = PayStatus.PAID,
            payMethod = PayMethod.CREDIT_CARD,
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