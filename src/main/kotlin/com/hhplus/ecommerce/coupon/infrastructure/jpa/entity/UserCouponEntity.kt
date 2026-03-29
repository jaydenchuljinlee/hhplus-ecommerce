package com.hhplus.ecommerce.coupon.infrastructure.jpa.entity

import com.hhplus.ecommerce.coupon.common.UserCouponStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_coupon",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "coupon_policy_id"])]
)
class UserCouponEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "coupon_policy_id", nullable = false)
    var couponPolicyId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserCouponStatus = UserCouponStatus.ISSUED,

    @Column(name = "issued_at", nullable = false)
    var issuedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null,

    @Column(name = "order_id")
    var orderId: Long? = null
) {
    fun use(orderId: Long) {
        require(status == UserCouponStatus.ISSUED) { "사용 가능한 쿠폰이 아닙니다. status=$status" }
        this.status = UserCouponStatus.USED
        this.usedAt = LocalDateTime.now()
        this.orderId = orderId
    }

    fun cancel() {
        require(status == UserCouponStatus.USED) { "사용된 쿠폰만 취소할 수 있습니다. status=$status" }
        this.status = UserCouponStatus.ISSUED
        this.usedAt = null
        this.orderId = null
    }

    fun expire() {
        if (status == UserCouponStatus.ISSUED) {
            this.status = UserCouponStatus.EXPIRED
        }
    }
}
