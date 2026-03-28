package com.hhplus.ecommerce.common.config

import com.hhplus.ecommerce.outboxevent.infrastructure.event.dto.OutboxEventInfo
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.ExponentialBackOff

/**
 * Kafka Consumer 에러 핸들링 & DLQ 설정
 *
 * ## 재시도 정책 (Exponential Backoff)
 * - 초기 대기: 2초
 * - 배수: 2배 (2s → 4s → 8s → ...)
 * - 최대 단일 간격: 10초
 * - 최대 경과 시간: 20초 (약 3회 재시도 후 중단)
 *
 * ## DLT (Dead Letter Topic) 라우팅
 * - 최대 재시도 초과 시 → `{원본 토픽}.DLT` 토픽으로 자동 라우팅
 * - 토픽 목록:
 *   - `BALANCE_HISTORY.DLT`
 *   - `PAY_HISTORY.DLT`
 *   - `PRODCUT_STOCK.DLT`
 *   - `ORDER_STOCK_FAIL.DLT`
 */
@Configuration
class KafkaConsumerConfig(
    private val kafkaTemplate: KafkaTemplate<String, OutboxEventInfo>,
    private val consumerFactory: ConsumerFactory<String, OutboxEventInfo>
) {
    companion object {
        private val logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)

        /** DLT 토픽 네이밍 규칙: {원본 토픽}.DLT */
        private const val DLT_SUFFIX = ".DLT"

        /** 초기 재시도 대기 시간 (ms) */
        private const val INITIAL_INTERVAL = 2_000L

        /** 재시도 대기 시간 증가 배수 */
        private const val MULTIPLIER = 2.0

        /** 단일 재시도 최대 대기 시간 (ms) */
        private const val MAX_INTERVAL = 10_000L

        /**
         * 전체 재시도 최대 경과 시간 (ms)
         * 2s + 4s + 8s = 14s → 20s 이내에서 약 3회 재시도
         */
        private const val MAX_ELAPSED_TIME = 20_000L
    }

    /**
     * 커스텀 Kafka Listener Container Factory
     *
     * Spring Boot 기본 팩토리를 대체하여 에러 핸들러(재시도 + DLT 라우팅)를 적용한다.
     * `@KafkaListener(containerFactory)` 미지정 시 이 팩토리가 기본으로 사용된다.
     */
    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, OutboxEventInfo> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, OutboxEventInfo>()
        factory.consumerFactory = consumerFactory
        factory.setCommonErrorHandler(errorHandler())
        return factory
    }

    /**
     * 재시도 + DLT 에러 핸들러
     *
     * Consumer 에서 예외가 전파되면:
     * 1. Exponential Backoff 정책에 따라 재시도
     * 2. 최대 경과 시간 초과 시 → [DeadLetterPublishingRecoverer] 로 DLT 라우팅
     */
    private fun errorHandler(): DefaultErrorHandler {
        val backOff = ExponentialBackOff(INITIAL_INTERVAL, MULTIPLIER).apply {
            maxInterval = MAX_INTERVAL
            maxElapsedTime = MAX_ELAPSED_TIME
        }
        return DefaultErrorHandler(deadLetterRecoverer(), backOff)
    }

    /**
     * DLT(Dead Letter Topic) 발행 복구기
     *
     * 최대 재시도 초과 메시지를 `{원본 토픽}.DLT` 토픽으로 라우팅한다.
     * 파티션 -1: Kafka 프로듀서 기본 라우팅 (키 해시 또는 라운드로빈)
     */
    private fun deadLetterRecoverer(): DeadLetterPublishingRecoverer {
        return DeadLetterPublishingRecoverer(kafkaTemplate) { record, ex ->
            val dltTopic = "${record.topic()}$DLT_SUFFIX"
            logger.warn(
                "KAFKA:DLT:ROUTE topic={}, key={}, dltTopic={}, error={}",
                record.topic(), record.key(), dltTopic, ex.message
            )
            TopicPartition(dltTopic, -1)
        }
    }
}
