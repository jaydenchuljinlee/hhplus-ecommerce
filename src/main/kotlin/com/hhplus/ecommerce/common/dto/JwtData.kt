package com.hhplus.ecommerce.common.dto

data class JwtData(
    var userId: Long,
    var email: String
) {
    companion object {
        fun of(userId: Long, email: String): JwtData {
            return JwtData(userId, email)
        }
    }
}