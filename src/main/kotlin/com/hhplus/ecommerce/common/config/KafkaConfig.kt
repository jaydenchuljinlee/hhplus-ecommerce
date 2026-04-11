package com.hhplus.ecommerce.common.config

import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.ExponentialBackOff

/**
 * Q65: 지수 백오프 재시도 + DLQ(Dead Letter Queue) 설정
 *
 * 동작 흐름:
 *  1. 처리 실패 시 지수 백오프로 재시도 (1s → 2s → 4s, 최대 3회)
 *  2. 3회 실패 후 DLQ Topic으로 이동 ({topic}.DLT, 예: PAY_HISTORY.DLT)
 *  3. DLQ 메시지에는 원본 payload + 실패 원인 헤더가 자동 포함됨
 *
 * Spring Boot는 ApplicationContext에 DefaultErrorHandler 빈이 존재하면
 * auto-configured ConcurrentKafkaListenerContainerFactory에 자동으로 주입한다.
 */
@Configuration
class KafkaConfig(
    private val kafkaTemplate: KafkaTemplate<String, OutboxEventInfo>
) {

    @Bean
    fun defaultErrorHandler(): DefaultErrorHandler {
        // 지수 백오프: 초기 1초, 배수 2.0 → 1s, 2s, 4s (최대 7초, 3회 시도)
        val backOff = ExponentialBackOff(1_000L, 2.0).apply {
            maxElapsedTime = 7_000L
        }

        // DeadLetterPublishingRecoverer: 재시도 소진 시 {topic}.DLT 로 자동 발행
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate)

        return DefaultErrorHandler(recoverer, backOff)
    }
}
