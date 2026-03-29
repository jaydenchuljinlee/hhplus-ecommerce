package com.hhplus.ecommerce.notification.domain

import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NotificationService {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun send(event: NotificationEvent) {
        // stub: 실제 발송 로직은 추후 SMS/Email/Push 서비스 연동
        logger.info(
            "NOTIFICATION:SEND - type=${event.type}, channel=${event.channel}, " +
            "userId=${event.userId}, orderId=${event.orderId}, title=${event.title}"
        )
    }
}
