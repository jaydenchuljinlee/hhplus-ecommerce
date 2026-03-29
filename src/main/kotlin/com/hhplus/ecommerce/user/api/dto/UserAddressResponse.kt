package com.hhplus.ecommerce.user.api.dto

import com.hhplus.ecommerce.user.domain.dto.UserAddressResult

data class UserAddressResponse(
    val id: Long,
    val alias: String,
    val receiverName: String,
    val phone: String,
    val address: String,
    val zipCode: String,
    val isDefault: Boolean
) {
    companion object {
        fun from(result: UserAddressResult) = UserAddressResponse(
            id = result.id,
            alias = result.alias,
            receiverName = result.receiverName,
            phone = result.phone,
            address = result.address,
            zipCode = result.zipCode,
            isDefault = result.isDefault
        )
    }
}
