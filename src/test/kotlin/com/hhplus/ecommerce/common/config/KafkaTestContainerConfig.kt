package com.hhplus.ecommerce.common.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class KafkaTestContainerConfig {
    companion object {
        private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"))
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withReuse(true)

        init {
            kafkaContainer.start()

            // Spring Kafka가 올바른 브로커를 참조할 수 있도록 동적으로 bootstrapServers 설정
            val bootstrapServers = kafkaContainer.bootstrapServers
            System.setProperty("spring.kafka.bootstrap-servers", bootstrapServers)
        }
    }

    @Bean
    fun kafkaContainer(): KafkaContainer {
        return kafkaContainer
    }
}