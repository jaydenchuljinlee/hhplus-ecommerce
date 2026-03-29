package com.hhplus.ecommerce.notification.domain.dto

import com.hhplus.ecommerce.notification.common.NotificationChannel
import com.hhplus.ecommerce.notification.common.NotificationType

data class NotificationEvent(
    val userId: Long,
    val type: NotificationType,
    val channel: NotificationChannel,
    val title: String,
    val body: String,
    val orderId: Long? = null
)
