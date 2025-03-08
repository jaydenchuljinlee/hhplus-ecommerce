package com.hhplus.ecommerce.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "hhplus.kafka.product")
class ProductStockKafkaProperties {
    lateinit var groupId: String
    lateinit var topic: String
}