package com.hhplus.ecommerce.domain.user.dto

import com.hhplus.ecommerce.infrastructure.user.jpa.entity.UserEntity

data class UserResult(
    var userId: Long,
    var userName: String,
    var phone: String
) {
    companion object {
        fun from(entity: UserEntity): UserResult {
            return UserResult(
                userId = entity.id,
                userName = entity.name,
                phone = entity.phone,
            )
        }
    }
}