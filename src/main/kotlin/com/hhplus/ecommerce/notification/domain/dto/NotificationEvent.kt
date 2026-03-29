package com.hhplus.ecommerce.notification.domain.dto

import com.hhplus.ecommerce.notification.common.NotificationChannel
import com.hhplus.ecommerce.notification.common.NotificationType

/**
 * 알림 이벤트 페이로드 (Kafka 메시지 본문)
 *
 * @param userId   수신자 사용자 ID
 * @param type     알림 유형
 * @param channel  발송 채널 (SMS / EMAIL / PUSH)
 * @param body     알림 본문
 * @param orderId  관련 주문 ID (선택)
 */
data class NotificationEvent(
    val userId: Long,
    val type: NotificationType,
    val channel: NotificationChannel,
    val title: String,
    val body: String,
    val orderId: Long? = null
) {
    companion object {
        fun orderPlaced(userId: Long, orderId: Long) = NotificationEvent(
            userId = userId,
            type = NotificationType.ORDER_PLACED,
            channel = NotificationChannel.PUSH,
            title = NotificationType.ORDER_PLACED.title,
            body = "주문(#$orderId)이 정상적으로 접수되었습니다.",
            orderId = orderId
        )

        fun paymentConfirmed(userId: Long, orderId: Long, amount: Long) = NotificationEvent(
            userId = userId,
            type = NotificationType.PAYMENT_CONFIRMED,
            channel = NotificationChannel.PUSH,
            title = NotificationType.PAYMENT_CONFIRMED.title,
            body = "주문(#$orderId) ${amount}원 결제가 완료되었습니다.",
            orderId = orderId
        )

        fun shipped(userId: Long, orderId: Long, trackingNumber: String) = NotificationEvent(
            userId = userId,
            type = NotificationType.SHIPPED,
            channel = NotificationChannel.PUSH,
            title = NotificationType.SHIPPED.title,
            body = "주문(#$orderId) 상품이 발송되었습니다. 운송장: $trackingNumber",
            orderId = orderId
        )

        fun delivered(userId: Long, orderId: Long) = NotificationEvent(
            userId = userId,
            type = NotificationType.DELIVERED,
            channel = NotificationChannel.PUSH,
            title = NotificationType.DELIVERED.title,
            body = "주문(#$orderId) 배송이 완료되었습니다.",
            orderId = orderId
        )
    }
}
