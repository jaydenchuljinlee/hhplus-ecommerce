package com.hhplus.ecommerce.payment.domain.repository

import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentEntity

interface IPaymentRepository {
    fun insertOrUpdate(entity: PaymentEntity): PaymentEntity
}