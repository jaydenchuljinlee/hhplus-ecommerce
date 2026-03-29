package com.hhplus.ecommerce.coupon.infrastructure.jpa.entity

import com.hhplus.ecommerce.coupon.common.CouponType
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponSoldOutException
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

/**
 * 쿠폰 정책 엔티티
 *
 * 쿠폰의 발급 조건, 할인 정책, 수량 등을 관리한다.
 */
@Entity
@Table(name = "coupon_policy")
class CouponPolicyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: CouponType,

    /** 할인 금액(FIXED) 또는 할인율(RATE, 1~100) */
    @Column(name = "discount_value", nullable = false)
    var discountValue: Long,

    /** 최소 주문 금액 */
    @Column(name = "min_order_amount", nullable = false)
    var minOrderAmount: Long = 0L,

    /** RATE 타입의 최대 할인 금액 상한 */
    @Column(name = "max_discount_amount")
    var maxDiscountAmount: Long? = null,

    /** 총 발급 가능 수량 (0 = 무제한) */
    @Column(name = "total_quantity", nullable = false)
    var totalQuantity: Int = 0,

    /** 현재까지 발급된 수량 */
    @Column(name = "issued_quantity", nullable = false)
    var issuedQuantity: Int = 0,

    @Column(name = "start_at", nullable = false)
    var startAt: LocalDateTime,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @Column(name = "created_at") @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    /** 발급 가능 여부 */
    fun isAvailable(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return now.isAfter(startAt) && now.isBefore(expiredAt)
    }

    /** 수량 잔여 여부 (totalQuantity == 0 이면 무제한) */
    fun hasStock(): Boolean = totalQuantity == 0 || issuedQuantity < totalQuantity

    /**
     * 선착순 발급 처리 — issuedQuantity 증가
     * @throws CouponSoldOutException 수량 소진 시
     */
    fun issue() {
        if (!hasStock()) throw CouponSoldOutException()
        issuedQuantity++
        updatedAt = LocalDateTime.now()
    }

    /**
     * 주문 금액에 대한 실제 할인 금액 계산
     */
    fun calculateDiscount(orderAmount: Long): Long {
        if (orderAmount < minOrderAmount) return 0L
        return when (type) {
            CouponType.FIXED -> discountValue.coerceAtMost(orderAmount)
            CouponType.RATE -> {
                val discount = orderAmount * discountValue / 100
                maxDiscountAmount?.let { discount.coerceAtMost(it) } ?: discount
            }
            CouponType.FREE_SHIPPING -> 0L // 배송비 별도 처리
        }
    }
}
