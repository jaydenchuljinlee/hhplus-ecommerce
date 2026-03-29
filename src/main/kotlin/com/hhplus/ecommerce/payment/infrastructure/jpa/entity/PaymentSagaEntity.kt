package com.hhplus.ecommerce.payment.infrastructure.jpa.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

/**
 * 결제 Saga 추적 엔티티
 *
 * 각 결제 단계의 상태를 기록하여 보상 트랜잭션 추적과 수동 복구를 지원한다.
 */
@Entity
@Table(
    name = "payment_saga",
    indexes = [
        Index(name = "idx_payment_saga_order_id", columnList = "order_id"),
        Index(name = "idx_payment_saga_status", columnList = "saga_status")
    ]
)
class PaymentSagaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", nullable = false)
    var sagaStatus: PaymentSagaStatus = PaymentSagaStatus.STARTED,

    /** 잔액 차감에 사용된 잔액 ID (환불 보상 시 역추적용) */
    @Column(name = "balance_id")
    var balanceId: Long? = null,

    /** 생성된 결제 ID */
    @Column(name = "payment_id")
    var paymentId: Long? = null,

    /** 실패 사유 */
    @Column(name = "fail_reason", length = 500)
    var failReason: String? = null,

    @Column(name = "created_at") @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
) {
    fun transition(status: PaymentSagaStatus, failReason: String? = null) {
        this.sagaStatus = status
        this.failReason = failReason
        this.updatedAt = LocalDateTime.now()
    }
}
