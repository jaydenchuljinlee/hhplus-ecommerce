package com.hhplus.ecommerce.common.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(KafkaTestContainerConfig::class, MySqlTestContainerConfig::class, RedisTestContainerConfig::class)
@SpringBootTest
class IntegrationConfig {
}