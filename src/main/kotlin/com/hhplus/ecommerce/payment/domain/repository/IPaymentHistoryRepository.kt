package com.hhplus.ecommerce.payment.domain.repository

import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentHistoryEntity

interface IPaymentHistoryRepository {
    fun findAllByUserId(userId: Long): List<PaymentHistoryEntity>
    fun insertOrUpdate(entity: PaymentHistoryEntity): PaymentHistoryEntity
}