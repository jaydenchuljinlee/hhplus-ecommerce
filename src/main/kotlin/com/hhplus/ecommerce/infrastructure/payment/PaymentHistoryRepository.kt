package com.hhplus.ecommerce.infrastructure.payment

import com.hhplus.ecommerce.infrastructure.payment.jpa.PaymentHistoryJpaRepository
import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentHistoryEntity
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