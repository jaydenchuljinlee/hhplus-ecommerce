package com.hhplus.ecommerce.infrastructure.payment.jpa

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentHistoryJpaRepository: JpaRepository<PaymentHistoryEntity, Long> {
    fun findByUserId(userId: Long): List<PaymentHistoryEntity>
}