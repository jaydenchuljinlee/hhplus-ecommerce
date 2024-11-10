package com.hhplus.ecommerce.common.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class RedissonConfig {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://localhost:6379") // Docker에서 Redis가 실행 중인 호스트
            .setConnectionMinimumIdleSize(10)
            .setConnectionPoolSize(64)

        return Redisson.create(config)
    }
}