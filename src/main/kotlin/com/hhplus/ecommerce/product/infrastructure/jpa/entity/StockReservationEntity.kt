package com.hhplus.ecommerce.product.infrastructure.jpa.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

/**
 * 예약 재고 엔티티
 *
 * 주문 생성 시 soft reserve → 결제 완료 시 commit → 취소/만료 시 release
 */
@Entity
@Table(
    name = "stock_reservation",
    indexes = [
        Index(name = "idx_stock_reservation_order_id", columnList = "order_id"),
        Index(name = "idx_stock_reservation_expired_at", columnList = "expired_at")
    ]
)
class StockReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "product_detail_id", nullable = false)
    val productDetailId: Long,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "expired_at", nullable = false)
    val expiredAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: StockReservationStatus = StockReservationStatus.RESERVED,

    @Column(name = "created_at") @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
) {
    fun commit() {
        status = StockReservationStatus.COMMITTED
        updatedAt = LocalDateTime.now()
    }

    fun release() {
        status = StockReservationStatus.RELEASED
        updatedAt = LocalDateTime.now()
    }

    fun expire() {
        status = StockReservationStatus.EXPIRED
        updatedAt = LocalDateTime.now()
    }
}
