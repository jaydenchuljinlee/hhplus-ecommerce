package com.hhplus.ecommerce.infrastructure.payment.jpa

import com.hhplus.ecommerce.infrastructure.payment.jpa.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository: JpaRepository<PaymentEntity, Long> {
}