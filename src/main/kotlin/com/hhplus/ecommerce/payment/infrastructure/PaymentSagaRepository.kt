package com.hhplus.ecommerce.payment.infrastructure

import com.hhplus.ecommerce.payment.common.PaymentSagaStatus
import com.hhplus.ecommerce.payment.domain.repository.IPaymentSagaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.PaymentSagaJpaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentSagaEntity
import org.springframework.stereotype.Component

@Component
class PaymentSagaRepository(
    private val jpaRepository: PaymentSagaJpaRepository
) : IPaymentSagaRepository {

    override fun save(entity: PaymentSagaEntity): PaymentSagaEntity =
        jpaRepository.save(entity)

    override fun findByOrderId(orderId: Long): PaymentSagaEntity? =
        jpaRepository.findByOrderId(orderId)

    override fun findAllByStatus(status: PaymentSagaStatus): List<PaymentSagaEntity> =
        jpaRepository.findAllBySagaStatus(status)
}
