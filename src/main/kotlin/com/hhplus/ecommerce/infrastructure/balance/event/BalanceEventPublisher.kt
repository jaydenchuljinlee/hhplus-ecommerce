package com.hhplus.ecommerce.infrastructure.balance.event

import com.hhplus.ecommerce.infrastructure.balance.mongodb.BalanceHistoryDocument

interface BalanceEventPublisher {
    fun publish(event: BalanceHistoryDocument)
}