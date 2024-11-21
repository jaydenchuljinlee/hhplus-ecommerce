package com.hhplus.ecommerce.infrastructure.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaTestConsumer {
    var message: String? = null

    @KafkaListener(topics = ["KAFKA_TOPIC"])
    fun listen(message: String) {
        println("message => $message")
        this.message = message
    }

}