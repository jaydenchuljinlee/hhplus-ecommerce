package com.hhplus.ecommerce.user.api.dto

data class UserAddressRequest(
    val userId: Long,
    val alias: String,
    val receiverName: String,
    val phone: String,
    val address: String,
    val zipCode: String,
    val isDefault: Boolean = false
)
