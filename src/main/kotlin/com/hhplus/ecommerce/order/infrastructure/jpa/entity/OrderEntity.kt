package com.hhplus.ecommerce.order.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.order.common.OrderStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity @Table(name = "order_info")
class OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "user_id")
    var userId: Long,
    @Column(name = "total_price")
    var totalPrice: Long,
    @Column(name = "total_quantity")
    var totalQuantity: Int,
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: OrderStatus = OrderStatus.REQUESTED,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orderDetails: MutableList<OrderDetailEntity> = mutableListOf(),
)