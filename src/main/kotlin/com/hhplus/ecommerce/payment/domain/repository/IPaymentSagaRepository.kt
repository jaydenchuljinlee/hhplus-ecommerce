package com.hhplus.ecommerce.payment.domain.repository

import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaStatus

interface IPaymentSagaRepository {
    fun save(entity: PaymentSagaEntity): PaymentSagaEntity
    fun findByOrderId(orderId: Long): PaymentSagaEntity?
    fun findAllByStatus(status: PaymentSagaStatus): List<PaymentSagaEntity>
}
