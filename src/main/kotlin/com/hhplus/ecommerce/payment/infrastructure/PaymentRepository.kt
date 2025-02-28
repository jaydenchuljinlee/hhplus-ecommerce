package com.hhplus.ecommerce.payment.infrastructure

import com.hhplus.ecommerce.payment.domain.repository.IPaymentRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.PaymentJpaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentEntity
import org.springframework.stereotype.Repository

@Repository
class PaymentRepository(
    private val paymentJpaRepository: PaymentJpaRepository
): IPaymentRepository {
    override fun insertOrUpdate(entity: PaymentEntity): PaymentEntity {
        return paymentJpaRepository.save(entity)
    }
}