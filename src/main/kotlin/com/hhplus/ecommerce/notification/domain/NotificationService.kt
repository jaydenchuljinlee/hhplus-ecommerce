package com.hhplus.ecommerce.notification.domain

import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 알림 발송 서비스 (Stub 구현)
 *
 * 실제 SMS / Email / Push 발송 연동은 외부 서비스 계약 후 구현한다.
 * 현재는 채널별 발송 시뮬레이션 로그만 출력한다.
 */
@Service
class NotificationService {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun send(event: NotificationEvent) {
        when (event.channel) {
            com.hhplus.ecommerce.notification.common.NotificationChannel.SMS ->
                logger.info("[SMS STUB] userId={}, title={}, body={}", event.userId, event.title, event.body)
            com.hhplus.ecommerce.notification.common.NotificationChannel.EMAIL ->
                logger.info("[EMAIL STUB] userId={}, title={}, body={}", event.userId, event.title, event.body)
            com.hhplus.ecommerce.notification.common.NotificationChannel.PUSH ->
                logger.info("[PUSH STUB] userId={}, title={}, body={}", event.userId, event.title, event.body)
        }
    }
}
