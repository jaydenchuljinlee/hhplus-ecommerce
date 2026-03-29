package com.hhplus.ecommerce.user.infrastructure.jpa.entity

import com.hhplus.ecommerce.common.enums.StateYn
import com.hhplus.ecommerce.user.common.UserGrade
import com.hhplus.ecommerce.user.common.UserStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity @Table(name = "user_info")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Column(name = "name")
    var name: String,
    @Column(name = "phone")
    var phone: String,

    /** 사용자 등급 — 누적 구매 금액 기준으로 자동 산정 */
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    var grade: UserGrade = UserGrade.NORMAL,

    /** 누적 결제 금액 (구매 확정 기준) */
    @Column(name = "total_purchase_amount", nullable = false)
    var totalPurchaseAmount: Long = 0L,

    /** 계정 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
) {
    /**
     * 결제 완료 후 누적 구매 금액을 반영하고 등급을 재산정한다.
     */
    fun addPurchaseAmount(amount: Long) {
        totalPurchaseAmount += amount
        grade = UserGrade.of(totalPurchaseAmount)
        updatedAt = LocalDateTime.now()
    }
}