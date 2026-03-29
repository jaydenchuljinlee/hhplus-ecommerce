package com.hhplus.ecommerce.coupon.infrastructure.jpa.entity

import com.hhplus.ecommerce.coupon.common.CouponType
import com.hhplus.ecommerce.common.enums.StateYn
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "coupon_policy")
class CouponPolicyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "name", nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: CouponType,

    @Column(name = "discount_value", nullable = false)
    var discountValue: Long,

    @Column(name = "min_order_amount")
    var minOrderAmount: Long = 0L,

    @Column(name = "max_discount_amount")
    var maxDiscountAmount: Long? = null,

    @Column(name = "total_quantity", nullable = false)
    var totalQuantity: Int,

    @Column(name = "issued_quantity", nullable = false)
    var issuedQuantity: Int = 0,

    @Column(name = "start_at", nullable = false)
    var startAt: LocalDateTime,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn")
    var delYn: StateYn = StateYn.N
) {
    fun issue() {
        require(issuedQuantity < totalQuantity) { "쿠폰 재고가 소진되었습니다." }
        issuedQuantity++
    }

    fun isAvailable(): Boolean {
        val now = LocalDateTime.now()
        return delYn == StateYn.N
            && now.isAfter(startAt)
            && now.isBefore(expiredAt)
            && issuedQuantity < totalQuantity
    }

    fun calculateDiscount(orderAmount: Long): Long {
        if (orderAmount < minOrderAmount) return 0L
        val discount = when (type) {
            CouponType.FIXED -> discountValue
            CouponType.RATE -> orderAmount * discountValue / 100
            CouponType.FREE_SHIPPING -> discountValue
        }
        return maxDiscountAmount?.let { minOf(discount, it) } ?: discount
    }
}
