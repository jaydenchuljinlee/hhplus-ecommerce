package com.hhplus.ecommerce.balance.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryDocument
import com.hhplus.ecommerce.balance.infrastructure.mongodb.BalanceHistoryMongoRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.OutboxEventRepository
import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.OutboxEventEntity
import com.hhplus.ecommerce.outboxevent.infrastructure.jpa.entity.enums.OutboxEventStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.any
import org.springframework.kafka.support.Acknowledgment
import java.util.UUID

/**
 * Q64: eventId 멱등성 검증
 * Q68: manual commit (Acknowledgment 제어)
 */
@ExtendWith(MockitoExtension::class)
class BalanceKafkaConsumerTest {

    @Mock private lateinit var balanceHistoryMongoRepository: BalanceHistoryMongoRepository
    @Mock private lateinit var outboxEventRepository: OutboxEventRepository
    @Mock private lateinit var objectMapper: ObjectMapper
    @Mock private lateinit var ack: Acknowledgment

    private lateinit var consumer: BalanceKafkaConsumer

    private val eventId = UUID.randomUUID()
    private val validPayload = """{"balanceId":1,"amount":5000,"balance":15000,"transactionType":"CHARGE"}"""

    @BeforeEach
    fun setUp() {
        consumer = BalanceKafkaConsumer(balanceHistoryMongoRepository, outboxEventRepository, objectMapper)
    }

    private fun outboxEntity(status: OutboxEventStatus) = OutboxEventEntity(
        id = eventId,
        groupId = "OUTBOX",
        topic = "BALANCE_HISTORY",
        payload = validPayload,
        status = status
    )

    private fun event(version: String = "1") = OutboxEventInfo(
        id = eventId,
        groupId = "OUTBOX",
        topic = "BALANCE_HISTORY",
        payload = validPayload,
        schemaVersion = version
    )

    @Nested
    @DisplayName("Q68: manual commit — 성공 시 ack 호출")
    inner class ManualCommit {

        @Test
        @DisplayName("정상 처리 완료 시 ack.acknowledge()를 호출한다")
        fun acknowledgeOnSuccess() {
            val document = BalanceHistoryDocument(balanceId = 1L, amount = 5000L, balance = 15000L, transactionType = "CHARGE")
            given(outboxEventRepository.findById(eventId)).willReturn(outboxEntity(OutboxEventStatus.PUBLISH))
            given(objectMapper.readValue(validPayload, BalanceHistoryDocument::class.java)).willReturn(document)

            consumer.listener(event(), ack)

            then(ack).should().acknowledge()
        }

        @Test
        @DisplayName("처리 실패 시 ack.acknowledge()를 호출하지 않는다 — DLQ 재시도 대상")
        fun noAcknowledgeOnFailure() {
            val document = BalanceHistoryDocument(id = "ERROR", balanceId = 1L, amount = 5000L, balance = 15000L, transactionType = "CHARGE")
            given(outboxEventRepository.findById(eventId)).willReturn(outboxEntity(OutboxEventStatus.PUBLISH))
            given(objectMapper.readValue(validPayload, BalanceHistoryDocument::class.java)).willReturn(document)

            consumer.listener(event(), ack)

            then(ack).should(never()).acknowledge()
        }

        @Test
        @DisplayName("지원하지 않는 schemaVersion이면 ack를 호출하고 처리를 건너뛴다")
        fun acknowledgeOnUnsupportedVersion() {
            consumer.listener(event(version = "99"), ack)

            then(ack).should().acknowledge()
            then(balanceHistoryMongoRepository).should(never()).save(any())
        }
    }

    @Nested
    @DisplayName("Q64: eventId 멱등성 — 중복 이벤트 처리 방지")
    inner class Idempotency {

        @Test
        @DisplayName("이미 SUCCESS 상태인 이벤트는 비즈니스 로직을 실행하지 않고 ack만 호출한다")
        fun skipDuplicateEvent() {
            given(outboxEventRepository.findById(eventId)).willReturn(outboxEntity(OutboxEventStatus.SUCCESS))

            consumer.listener(event(), ack)

            then(balanceHistoryMongoRepository).should(never()).save(any())
            then(ack).should().acknowledge()
        }

        @Test
        @DisplayName("처리 성공 후 OutboxEvent 상태가 SUCCESS로 업데이트된다")
        fun updateStatusToSuccessAfterProcessing() {
            val entity = outboxEntity(OutboxEventStatus.PUBLISH)
            val document = BalanceHistoryDocument(balanceId = 1L, amount = 5000L, balance = 15000L, transactionType = "CHARGE")
            given(outboxEventRepository.findById(eventId)).willReturn(entity)
            given(objectMapper.readValue(validPayload, BalanceHistoryDocument::class.java)).willReturn(document)

            consumer.listener(event(), ack)

            assert(entity.status == OutboxEventStatus.SUCCESS)
            then(outboxEventRepository).should().insertOrUpdate(entity)
        }

        @Test
        @DisplayName("처리 실패 시 OutboxEvent 상태가 FAILED로 업데이트된다")
        fun updateStatusToFailedOnError() {
            val entity = outboxEntity(OutboxEventStatus.PUBLISH)
            val document = BalanceHistoryDocument(id = "ERROR", balanceId = 1L, amount = 5000L, balance = 15000L, transactionType = "CHARGE")
            given(outboxEventRepository.findById(eventId)).willReturn(entity)
            given(objectMapper.readValue(validPayload, BalanceHistoryDocument::class.java)).willReturn(document)

            consumer.listener(event(), ack)

            assert(entity.status == OutboxEventStatus.FAILED)
            then(outboxEventRepository).should().insertOrUpdate(entity)
        }
    }
}
