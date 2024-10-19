package com.hhplus.ecommerce.infrastructure.payment

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity

interface IPaymentRepository {
    fun insertOrUpdate(entity: PaymentEntity): PaymentEntity
}