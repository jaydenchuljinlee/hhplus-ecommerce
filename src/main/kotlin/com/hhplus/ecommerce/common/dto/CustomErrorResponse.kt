package com.hhplus.ecommerce.common.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus

data class CustomErrorResponse(
    @Schema(description = "상태 코드", example = "500")
    val code: Int,
    @Schema(description = "상태 값", example = "INTERNAL_SERVER_ERROR")
    val status: HttpStatus,
    @Schema(description = "응답 메시지", example = "요청 실패")
    val message: String
) {
    companion object {
        fun fail(message: String): CustomErrorResponse {
            return CustomErrorResponse(
                code = 500,
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                message = message,
            )
        }
    }
}