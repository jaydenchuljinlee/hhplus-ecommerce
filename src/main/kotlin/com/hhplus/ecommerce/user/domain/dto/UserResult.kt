package com.hhplus.ecommerce.user.domain.dto

import com.hhplus.ecommerce.user.common.UserGrade
import com.hhplus.ecommerce.user.common.UserStatus
import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserEntity

data class UserResult(
    var userId: Long,
    var userName: String,
    var phone: String,
    var grade: UserGrade = UserGrade.NORMAL,
    var point: Long = 0L,
    var totalPurchaseAmount: Long = 0L,
    var status: UserStatus = UserStatus.ACTIVE
) {
    companion object {
        fun from(entity: UserEntity): UserResult {
            return UserResult(
                userId = entity.id,
                userName = entity.name,
                phone = entity.phone,
                grade = entity.grade,
                point = entity.point,
                totalPurchaseAmount = entity.totalPurchaseAmount,
                status = entity.status
            )
        }
    }
}