package com.hhplus.ecommerce.balance.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "balance_history")
class BalanceHistoryEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "balance_id")
    var balanceId: Long,
    @Column(name = "amount")
    var amount: Long,
    @Column(name = "balance")
    var balance: Long,
    @Column(name = "transaction_type")
    var transactionType: String,
    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
)