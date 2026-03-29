package com.hhplus.ecommerce.notification.domain

import com.hhplus.ecommerce.notification.domain.dto.NotificationEvent

interface INotificationEventPublisher {
    fun publish(event: NotificationEvent)
}
