package com.hhplus.ecommerce.infrastructure.balance.mongodb

import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "balance_history")
data class BalanceHistoryDocument(
    @Id
    var id: String? = null,
    var balanceId: Long,
    var amount: Long,
    var balance: Long,
    var transactionType: String,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    var delYn: StateYn = StateYn.N
)