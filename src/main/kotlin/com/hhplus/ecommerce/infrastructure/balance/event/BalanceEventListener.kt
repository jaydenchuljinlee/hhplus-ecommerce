package com.hhplus.ecommerce.infrastructure.balance.event

interface BalanceEventListener {
    fun listener(message: String)
}