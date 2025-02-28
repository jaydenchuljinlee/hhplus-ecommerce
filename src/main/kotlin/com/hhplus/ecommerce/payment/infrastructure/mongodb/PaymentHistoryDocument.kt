package com.hhplus.ecommerce.payment.infrastructure.mongodb

import com.fasterxml.jackson.annotation.JsonFormat
import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.payment.common.PayStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "payment_history")
data class PaymentHistoryDocument(
    @Id
    var id: String? = null,
    var paymentId: Long,
    var userId: Long,
    var price: Long,
    var status: PayStatus,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    var delYn: StateYn = StateYn.N
)