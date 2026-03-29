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

    /** CONFIRMED → PREPARING: 출고 준비 시작 */
    fun startPreparing() {
        if (status != OrderStatus.CONFIRMED) {
            throw InvalidOrderStatusException("출고 준비는 CONFIRMED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.PREPARING
        updatedAt = LocalDateTime.now()
    }

    /** PREPARING → READY_TO_SHIP: 출고 완료 */
    fun readyToShip() {
        if (status != OrderStatus.PREPARING) {
            throw InvalidOrderStatusException("출고 완료는 PREPARING 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.READY_TO_SHIP
        updatedAt = LocalDateTime.now()
    }

    /** READY_TO_SHIP → SHIPPED: 배송 시작 */
    fun ship() {
        if (status != OrderStatus.READY_TO_SHIP) {
            throw InvalidOrderStatusException("배송 시작은 READY_TO_SHIP 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.SHIPPED
        updatedAt = LocalDateTime.now()
    }

    /** SHIPPED → IN_TRANSIT: 배송 중 */
    fun startTransit() {
        if (status != OrderStatus.SHIPPED) {
            throw InvalidOrderStatusException("배송 중 전이는 SHIPPED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.IN_TRANSIT
        updatedAt = LocalDateTime.now()
    }

    /** IN_TRANSIT → DELIVERED: 배송 완료 */
    fun deliver() {
        if (status != OrderStatus.IN_TRANSIT) {
            throw InvalidOrderStatusException("배송 완료 전이는 IN_TRANSIT 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.DELIVERED
        updatedAt = LocalDateTime.now()
    }

    /** DELIVERED → PURCHASE_CONFIRMED: 구매 확정 */
    fun confirmPurchase() {
        if (status != OrderStatus.DELIVERED) {
            throw InvalidOrderStatusException("구매 확정은 DELIVERED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.PURCHASE_CONFIRMED
        updatedAt = LocalDateTime.now()
    }

    /** DELIVERED | PURCHASE_CONFIRMED → RETURN_REQUESTED: 반품 신청 */
    fun requestReturn() {
        if (status != OrderStatus.DELIVERED && status != OrderStatus.PURCHASE_CONFIRMED) {
            throw InvalidOrderStatusException("반품 신청은 DELIVERED 또는 PURCHASE_CONFIRMED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.RETURN_REQUESTED
        updatedAt = LocalDateTime.now()
    }

    /** RETURN_REQUESTED → RETURNED: 반품 완료 */
    fun completeReturn() {
        if (status != OrderStatus.RETURN_REQUESTED) {
            throw InvalidOrderStatusException("반품 완료는 RETURN_REQUESTED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.RETURNED
        updatedAt = LocalDateTime.now()
    }

    /** RETURNED → REFUND_PENDING: 환불 처리 중 */
    fun requestRefund() {
        if (status != OrderStatus.RETURNED) {
            throw InvalidOrderStatusException("환불 처리는 RETURNED 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.REFUND_PENDING
        updatedAt = LocalDateTime.now()
    }

    /** REFUND_PENDING → REFUNDED: 환불 완료 */
    fun completeRefund() {
        if (status != OrderStatus.REFUND_PENDING) {
            throw InvalidOrderStatusException("환불 완료는 REFUND_PENDING 상태에서만 가능합니다. (현재: $status)")
        }
        status = OrderStatus.REFUNDED
        updatedAt = LocalDateTime.now()
    }

    /** 어떤 상태에서든 CANCELED 로 전환 (단, PURCHASE_CONFIRMED 이후 불가) */
    fun cancel() {
        val nonCancelableStatuses = setOf(
            OrderStatus.PURCHASE_CONFIRMED, OrderStatus.RETURN_REQUESTED,
            OrderStatus.RETURNED, OrderStatus.REFUND_PENDING, OrderStatus.REFUNDED
        )
        if (status in nonCancelableStatuses) {
            throw InvalidOrderStatusException("$status 상태에서는 주문을 취소할 수 없습니다.")
        }
        status = OrderStatus.CANCELED
        updatedAt = LocalDateTime.now()
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