package com.hhplus.ecommerce.payment.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "payment_history")
class PaymentHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "payment_id")
    var paymentId: Long,
    @Column(name = "user_id")
    var userId: Long,
    @Column(name = "price")
    var price: Long,
    @Column(name = "status")
    var status: String,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
)