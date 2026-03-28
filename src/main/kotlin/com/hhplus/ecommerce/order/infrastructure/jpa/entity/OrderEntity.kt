package com.hhplus.ecommerce.order.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.order.common.OrderStatus
import com.hhplus.ecommerce.order.infrastructure.exception.InvalidOrderStatusException
import com.hhplus.ecommerce.order.infrastructure.exception.OrderDetailNotFoundException
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
) {
    /** REQUESTED → STOCK_CONFIRMED: 재고 확보 완료, 결제 가능 상태로 전환 */
    fun confirmStock() {
        if (status != OrderStatus.REQUESTED) {
            throw InvalidOrderStatusException("재고 확보 완료 전이는 REQUESTED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.STOCK_CONFIRMED
    }

    /** REQUESTED → STOCK_FAILED: 재고 확보 실패, 결제 불가 상태로 전환 */
    fun failStock() {
        if (status != OrderStatus.REQUESTED) {
            throw InvalidOrderStatusException("재고 확보 실패 전이는 REQUESTED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.STOCK_FAILED
    }

    /** STOCK_CONFIRMED → CONFIRMED: 결제 완료, 주문 최종 확정 */
    fun confirm() {
        if (status != OrderStatus.STOCK_CONFIRMED) {
            throw InvalidOrderStatusException("주문 확정은 STOCK_CONFIRMED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.CONFIRMED
    }

    /** 어떤 상태에서든 CANCELED 로 전환 (단, 이미 CONFIRMED 된 경우 제외) */
    fun cancel() {
        if (status == OrderStatus.CONFIRMED) {
            throw InvalidOrderStatusException("이미 확정된 주문은 취소할 수 없습니다.")
        }
        status = OrderStatus.CANCELED
    }

    fun removeOf(productId: Long) {
        val detail = this.orderDetails.firstOrNull { it.productId == productId }
            ?: throw OrderDetailNotFoundException("주문 상세에서 상품(productId=$productId)을 찾을 수 없습니다.")

        this.totalPrice -= (detail.price * detail.quantity)
        this.totalQuantity -= detail.quantity

        if (this.isZero()) {
            this.delYn = StateYn.Y
        }

        orderDetails.remove(detail)
    }

    fun isZero(): Boolean {
        return this.totalPrice <= 0  && this.totalQuantity <= 0
    }
}