package com.hhplus.ecommerce.payment.infrastructure

import com.hhplus.ecommerce.payment.domain.repository.IPaymentHistoryRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.PaymentHistoryJpaRepository
import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentHistoryEntity
import org.springframework.stereotype.Repository

@Repository
class PaymentHistoryRepository(
    private val paymentHistoryJpaRepository: PaymentHistoryJpaRepository
): IPaymentHistoryRepository {
    override fun findAllByUserId(userId: Long): List<PaymentHistoryEntity> {
        return paymentHistoryJpaRepository.findByUserId(userId)
    }

    override fun insertOrUpdate(entity: PaymentHistoryEntity): PaymentHistoryEntity {
        return paymentHistoryJpaRepository.save(entity)
    }
}