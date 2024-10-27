package com.hhplus.ecommerce.common.dto

data class JwtData(
    var userId: Long,
    var email: String,
    var role: String
) {
    companion object {
        fun of(userId: Long, email: String, role: String): JwtData {
            return JwtData(userId, email, role)
        }
    }
}