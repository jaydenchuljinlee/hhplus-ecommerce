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

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    var grade: UserGrade = UserGrade.NORMAL,

    @Column(name = "point")
    var point: Long = 0L,

    @Column(name = "total_purchase_amount")
    var totalPurchaseAmount: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "created_at") @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var delYn: StateYn = StateYn.N
) {
    fun addPurchaseAmount(amount: Long) {
        totalPurchaseAmount += amount
        grade = UserGrade.from(totalPurchaseAmount)
        updatedAt = LocalDateTime.now()
    }

    fun usePoint(amount: Long) {
        check(point >= amount) { "포인트가 부족합니다. 현재 포인트: $point, 요청 금액: $amount" }
        point -= amount
        updatedAt = LocalDateTime.now()
    }

    fun chargePoint(amount: Long) {
        point += amount
        updatedAt = LocalDateTime.now()
    }
}
