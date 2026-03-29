package com.hhplus.ecommerce.payment.infrastructure.jpa

import com.hhplus.ecommerce.payment.common.PaymentSagaStatus
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentSagaJpaRepository : JpaRepository<PaymentSagaEntity, Long> {
    fun findByOrderId(orderId: Long): PaymentSagaEntity?
    fun findAllBySagaStatus(status: PaymentSagaStatus): List<PaymentSagaEntity>
}
