package com.hhplus.ecommerce.user.domain.dto

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserEntity

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