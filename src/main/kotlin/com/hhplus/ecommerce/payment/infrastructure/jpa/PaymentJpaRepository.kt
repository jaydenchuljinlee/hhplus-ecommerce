package com.hhplus.ecommerce.payment.infrastructure.jpa

import com.hhplus.ecommerce.payment.infrastructure.jpa.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository: JpaRepository<PaymentEntity, Long> {
}