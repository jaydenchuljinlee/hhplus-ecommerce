package com.hhplus.ecommerce.common.interceptor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AdminInterceptor: HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val userId = request.getHeader("X-Admin-Id")
        if (userId == null || userId.isEmpty()) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = "application/json"
            val errorResponse = CustomErrorResponse.unAuthorized("인증에 실패했습니다.")
            response.writer.write(jacksonObjectMapper().writeValueAsString(errorResponse))
            return false
        }
        return true
    }
}