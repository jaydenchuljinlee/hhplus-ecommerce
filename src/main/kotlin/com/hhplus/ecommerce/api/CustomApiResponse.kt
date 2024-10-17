package com.hhplus.ecommerce.api

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus

data class CustomApiResponse<T>(
    @Schema(description = "상태 코드", example = "200")
    val code: Int,
    @Schema(description = "상태 값", example = "OK")
    val status: HttpStatus,
    @Schema(description = "응답 메시지", example = "요청 성공")
    val message: String,
    @Schema(description = "응답 데이터")
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
    }
}