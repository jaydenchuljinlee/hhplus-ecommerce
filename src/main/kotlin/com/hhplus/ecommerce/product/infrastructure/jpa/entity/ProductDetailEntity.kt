package com.hhplus.ecommerce.product.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity @Table(name = "product_detail")
class ProductDetailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "product_id")
    var productId: Long,
    @Column(name = "product_option_id")
    var productOptionId: Long,
    @Column(name = "quantity")
    var quantity: Int,

    /** 예약된 재고 수량 — soft reserve 적용 */
    @Column(name = "reserved_quantity", nullable = false)
    var reservedQuantity: Int = 0,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
) {
    /** 실제 구매 가능한 재고 (= quantity - reservedQuantity) */
    val availableQuantity: Int
        get() = quantity - reservedQuantity

    /** 기존 즉시 차감 방식 (하위 호환용) */
    fun decrease(amount: Int) {
        val newQuantity = quantity - amount
        require(newQuantity >= 0) { throw OutOfStockException() }
        quantity = newQuantity
    }

    /**
     * Soft Reserve: 주문 생성 시 가용 재고를 임시 점유한다.
     * availableQuantity 기준으로 검증하여 초과 판매를 방지한다.
     */
    fun reserve(amount: Int) {
        require(availableQuantity >= amount) { throw OutOfStockException() }
        reservedQuantity += amount
    }

    /**
     * Commit: 결제 완료 후 예약 재고를 실제 재고에서 차감 확정한다.
     */
    fun commit(amount: Int) {
        require(quantity >= amount) { throw OutOfStockException() }
        quantity -= amount
        reservedQuantity = (reservedQuantity - amount).coerceAtLeast(0)
    }

    /**
     * Release: 결제 취소 / 예약 만료 시 임시 점유를 해제한다.
     */
    fun release(amount: Int) {
        reservedQuantity = (reservedQuantity - amount).coerceAtLeast(0)
    }
}