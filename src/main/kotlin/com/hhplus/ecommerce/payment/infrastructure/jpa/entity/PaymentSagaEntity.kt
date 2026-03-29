package com.hhplus.ecommerce.payment.infrastructure.jpa.entity

import com.hhplus.ecommerce.payment.common.PaymentSagaStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment_saga")
class PaymentSagaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "order_id")
    var orderId: Long,

    @Column(name = "user_id")
    var userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status")
    var sagaStatus: PaymentSagaStatus = PaymentSagaStatus.STARTED,

    @Column(name = "payment_id")
    var paymentId: Long? = null,

    @Column(name = "fail_reason", columnDefinition = "TEXT")
    var failReason: String? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateStatus(status: PaymentSagaStatus, paymentId: Long? = null, failReason: String? = null) {
        this.sagaStatus = status
        paymentId?.let { this.paymentId = it }
        failReason?.let { this.failReason = it }
        this.updatedAt = LocalDateTime.now()
    }
}
