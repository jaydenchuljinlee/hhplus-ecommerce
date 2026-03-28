package com.hhplus.ecommerce.payment.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.common.properties.PaymentKafkaProperties
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.payment.domain.dto.PaymentResult
import com.hhplus.ecommerce.payment.domain.event.IPaymentEventPublisher
import com.hhplus.ecommerce.payment.infrastructure.mongodb.PaymentHistoryDocument
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 결제 이벤트 발행 구현체 (Infrastructure)
 *
 * [IPaymentEventPublisher]의 Infrastructure 구현체.
 * Outbox 패턴으로 결제 이력을 Kafka를 통해 MongoDB에 전달한다.
 *
 * @TransactionalEventListener(BEFORE_COMMIT) 이 활성화된 상태에서 호출되므로,
 * 반드시 활성 트랜잭션 내에서 호출되어야 한다.
 */
@Component
class PaymentEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val paymentKafkaProperties: PaymentKafkaProperties,
    private val objectMapper: ObjectMapper
) : IPaymentEventPublisher {

    override fun publishPay(result: PaymentResult) {
        val document = PaymentHistoryDocument(
            paymentId = result.paymentId,
            userId = result.userId,
            price = result.price,
            status = result.status
        )
        val outboxEvent = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = paymentKafkaProperties.groupId,
            topic = paymentKafkaProperties.topic,
            payload = objectMapper.writeValueAsString(document)
        )
        applicationEventPublisher.publishEvent(outboxEvent)
    }
}
