package com.hhplus.ecommerce.payment.infrastructure.jpa

import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentSagaJpaRepository : JpaRepository<PaymentSagaEntity, Long> {
    fun findByOrderId(orderId: Long): PaymentSagaEntity?
    fun findAllBySagaStatus(sagaStatus: PaymentSagaStatus): List<PaymentSagaEntity>
}
