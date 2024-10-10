package com.hhplus.ecommerce.api

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
) {
    companion object {
        fun<T> success(data: T? = null): ApiResponse<T> {
            return ApiResponse(
                code = 200,
                message = "Success",
                data = data
            )
        }

    }
}