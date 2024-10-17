package com.hhplus.ecommerce.infrastructure.payment

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentHistoryEntity

interface IPaymentHistoryRepository {
    fun findAllByUserId(userId: Long): List<PaymentHistoryEntity>
    fun insertOrUpdate(entity: PaymentHistoryEntity): PaymentHistoryEntity
}