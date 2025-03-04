package com.hhplus.ecommerce.common.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer

@TestConfiguration
class RedisTestContainerConfig {
    companion object {
        val redisContainer: GenericContainer<*> = GenericContainer("redis:latest").apply {
            withExposedPorts(6379)
            start()
        }
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val redisHost = redisContainer.host
        val redisPort = redisContainer.getMappedPort(6379)

        val config = Config()
        config.useSingleServer().apply {
            address = "redis://$redisHost:$redisPort"
            connectionMinimumIdleSize = 10
            connectionPoolSize = 64
        }
        config.lockWatchdogTimeout = 30000

        return Redisson.create(config)
    }
}