package com.hhplus.ecommerce.infrastructure.kafka

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class KafkaIntegrationTest {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    private lateinit var kafkaTestConsumer: KafkaTestConsumer

    @DisplayName("success: 카프카 연동 테스트")
    @Test
    fun testKafka() {
        // 1. 메시지 발행
        val topic = "KAFKA_TOPIC"
        val message = "카프카 메시지"
        kafkaTemplate.send(topic, message)

        // 2. 메시지 수신 검증
        Thread.sleep(3000) // 메시지 처리 대기
        assertEquals(message, kafkaTestConsumer.message)
    }

}