package com.hhplus.ecommerce.infrastructure.payment

import com.hhplus.ecommerce.domain.payment.repository.IPaymentRepository
import com.hhplus.ecommerce.infrastructure.payment.jpa.PaymentJpaRepository
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import org.springframework.stereotype.Repository

@Repository
class PaymentRepository(
    private val paymentJpaRepository: PaymentJpaRepository
): IPaymentRepository {
    override fun insertOrUpdate(entity: PaymentEntity): PaymentEntity {
        return paymentJpaRepository.save(entity)
    }
}