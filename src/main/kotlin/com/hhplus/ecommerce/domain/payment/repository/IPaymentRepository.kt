package com.hhplus.ecommerce.domain.payment.repository

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity

interface IPaymentRepository {
    fun insertOrUpdate(entity: PaymentEntity): PaymentEntity
}