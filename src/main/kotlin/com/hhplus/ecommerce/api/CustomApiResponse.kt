package com.hhplus.ecommerce.api

import org.springframework.http.HttpStatus

data class CustomApiResponse<T>(
    val code: Int,
    val status: HttpStatus,
    val message: String,
    val data: T? = null,

    ) {
    companion object {
        fun<T> success(data: T? = null): CustomApiResponse<T> {
            return CustomApiResponse(
                code = 200,
                status = HttpStatus.OK,
                message = "Success",
                data = data
            )
        }

        fun fail(message: String): CustomApiResponse<Any> {
            return CustomApiResponse(
                code = 500,
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                message = message,
                data = null
            )
        }
    }
}