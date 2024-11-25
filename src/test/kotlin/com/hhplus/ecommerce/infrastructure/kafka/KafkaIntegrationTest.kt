package com.hhplus.ecommerce.infrastructure.kafka

import com.hhplus.ecommerce.common.config.IntegrationConfig
import com.hhplus.ecommerce.infrastructure.outboxevent.event.dto.OutboxEventInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class KafkaIntegrationTest: IntegrationConfig() {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, OutboxEventInfo>

    @Autowired
    private lateinit var kafkaTestConsumer: KafkaTestConsumer


    @DisplayName("success: 카프카 연동 테스트")
    @Test
    fun testKafka() {
        // 1. 메시지 발행
        val topic = "BALANCE_HISTORY_TEST"
        val message = "카프카 메시지"

        val entity = OutboxEventInfo(
            id = UUID.randomUUID(),
            groupId = "OUTBOX_TEST",
            topic = topic,
            payload = message
        )

        kafkaTemplate.send(topic, entity).get() // 메시지 전송 완료 대기

        // 2. 메시지 수신 검증
        val success = kafkaTestConsumer.waitForMessage(1, TimeUnit.SECONDS) // 5초 대기
        assertTrue(success, "메시지 수신 실패")
        assertEquals(topic, kafkaTestConsumer.message)
    }

}