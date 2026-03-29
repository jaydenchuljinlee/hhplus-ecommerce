package com.hhplus.ecommerce.user.domain.dto

import com.hhplus.ecommerce.user.infrastructure.jpa.entity.UserAddressEntity
import java.time.LocalDateTime

data class UserAddressResult(
    val id: Long,
    val userId: Long,
    val alias: String,
    val receiverName: String,
    val phone: String,
    val address: String,
    val detailAddress: String?,
    val zipCode: String,
    val isDefault: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: UserAddressEntity) = UserAddressResult(
            id = entity.id,
            userId = entity.userId,
            alias = entity.alias,
            receiverName = entity.receiverName,
            phone = entity.phone,
            address = entity.address,
            detailAddress = entity.detailAddress,
            zipCode = entity.zipCode,
            isDefault = entity.isDefault,
            createdAt = entity.createdAt
        )
    }
}
