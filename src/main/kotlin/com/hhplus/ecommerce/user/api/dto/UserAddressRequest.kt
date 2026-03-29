package com.hhplus.ecommerce.user.api.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserAddressRequest(
    @field:Min(1) val userId: Long,
    @field:NotBlank @field:Size(max = 30) val alias: String,
    @field:NotBlank @field:Size(max = 50) val receiverName: String,
    @field:NotBlank @field:Size(max = 20) val phone: String,
    @field:NotBlank @field:Size(max = 200) val address: String,
    @field:Size(max = 100) val detailAddress: String? = null,
    @field:NotBlank @field:Size(max = 10) val zipCode: String,
    val isDefault: Boolean = false
)
