package com.hhplus.ecommerce.cart.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity @Table(name = "cart")
class CartEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "user_id")
    var userId: Long,
    @Column(name = "product_id")
    var productId: Long,
    @Column(name = "quantity")
    var quantity: Int,
    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
)