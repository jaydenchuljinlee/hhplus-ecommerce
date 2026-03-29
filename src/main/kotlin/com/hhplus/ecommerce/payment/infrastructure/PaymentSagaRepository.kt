package com.hhplus.ecommerce.payment.infrastructure

import com.hhplus.ecommerce.payment.domain.repository.IPaymentSagaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.PaymentSagaJpaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaStatus
import org.springframework.stereotype.Repository

@Repository
class PaymentSagaRepository(
    private val paymentSagaJpaRepository: PaymentSagaJpaRepository
) : IPaymentSagaRepository {

    override fun save(entity: PaymentSagaEntity): PaymentSagaEntity {
        return paymentSagaJpaRepository.save(entity)
    }

    override fun findByOrderId(orderId: Long): PaymentSagaEntity? {
        return paymentSagaJpaRepository.findByOrderId(orderId)
    }

    override fun findAllByStatus(status: PaymentSagaStatus): List<PaymentSagaEntity> {
        return paymentSagaJpaRepository.findAllBySagaStatus(status)
    }
}
