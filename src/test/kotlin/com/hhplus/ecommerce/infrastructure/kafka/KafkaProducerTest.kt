package com.hhplus.ecommerce.infrastructure.kafka

import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

/**
 * Q63: Partition Key 라우팅 검증
 * - partitionKey 있을 때 → key 포함 send(topic, key, value) 호출
 * - partitionKey 없을 때 → key 없이 send(topic, value) 호출 (기존 round-robin)
 */
@ExtendWith(MockitoExtension::class)
class KafkaProducerTest {

    @Mock
    private lateinit var kafkaTemplate: KafkaTemplate<String, OutboxEventInfo>

    private lateinit var kafkaProducer: KafkaProducer

    @BeforeEach
    fun setUp() {
        kafkaProducer = KafkaProducer(kafkaTemplate)
    }

    @Nested
    @DisplayName("Q63: partitionKey 라우팅")
    inner class PartitionKeyRouting {

        @Test
        @DisplayName("partitionKey가 있으면 key 포함 send를 호출한다")
        fun sendWithKeyWhenPartitionKeyPresent() {
            val event = OutboxEventInfo(
                id = UUID.randomUUID(),
                groupId = "OUTBOX",
                topic = "PAY_HISTORY",
                payload = "{}",
                partitionKey = "order-123"
            )

            kafkaProducer.sendOutboxEvent(event)

            then(kafkaTemplate).should().send(eq("PAY_HISTORY"), eq("order-123"), eq(event))
        }

        @Test
        @DisplayName("partitionKey가 null이면 key 없이 send를 호출한다 (round-robin)")
        fun sendWithoutKeyWhenPartitionKeyNull() {
            val event = OutboxEventInfo(
                id = UUID.randomUUID(),
                groupId = "OUTBOX",
                topic = "PAY_HISTORY",
                payload = "{}",
                partitionKey = null
            )

            kafkaProducer.sendOutboxEvent(event)

            then(kafkaTemplate).should().send(eq("PAY_HISTORY"), eq(event))
        }
    }
}
