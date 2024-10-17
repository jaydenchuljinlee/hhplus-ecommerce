package com.hhplus.ecommerce.infrastructure.payment.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class PaymentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "order_id")
    var orderId: Long,
    @Column(name = "status")
    var status: String,
    @Column(name = "price")
    var price: Long,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
)