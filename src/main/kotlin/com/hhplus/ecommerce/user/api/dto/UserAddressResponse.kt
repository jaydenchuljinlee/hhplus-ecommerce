package com.hhplus.ecommerce.user.api.dto

import com.hhplus.ecommerce.user.domain.dto.UserAddressResult
import java.time.LocalDateTime

data class UserAddressResponse(
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
        fun from(result: UserAddressResult) = UserAddressResponse(
            id = result.id,
            userId = result.userId,
            alias = result.alias,
            receiverName = result.receiverName,
            phone = result.phone,
            address = result.address,
            detailAddress = result.detailAddress,
            zipCode = result.zipCode,
            isDefault = result.isDefault,
            createdAt = result.createdAt
        )
    }
}
