package com.hhplus.ecommerce.coupon.infrastructure.jpa.entity

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import com.hhplus.ecommerce.coupon.infrastructure.exception.CouponAlreadyUsedException
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

/**
 * 사용자 발급 쿠폰 엔티티
 */
@Entity
@Table(
    name = "user_coupon",
    indexes = [
        Index(name = "idx_user_coupon_user_id", columnList = "user_id"),
        Index(name = "idx_user_coupon_policy_id", columnList = "coupon_policy_id")
    ]
)
class UserCouponEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "coupon_policy_id", nullable = false)
    val couponPolicyId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserCouponStatus = UserCouponStatus.ISSUED,

    @Column(name = "issued_at", nullable = false) @CreatedDate
    val issuedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null,

    /** 사용된 주문 ID */
    @Column(name = "order_id")
    var orderId: Long? = null
) {
    /**
     * 쿠폰 사용 처리
     * @throws CouponAlreadyUsedException 이미 사용된 쿠폰
     */
    fun use(orderId: Long) {
        if (status != UserCouponStatus.ISSUED) throw CouponAlreadyUsedException()
        this.status = UserCouponStatus.USED
        this.usedAt = LocalDateTime.now()
        this.orderId = orderId
    }

    /** 쿠폰 사용 취소 (주문 취소 시 보상) */
    fun cancel() {
        if (status == UserCouponStatus.USED) {
            this.status = UserCouponStatus.ISSUED
            this.usedAt = null
            this.orderId = null
        }
    }

    /** 쿠폰 만료 처리 */
    fun expire() {
        if (status == UserCouponStatus.ISSUED) {
            this.status = UserCouponStatus.EXPIRED
        }
    }
}
