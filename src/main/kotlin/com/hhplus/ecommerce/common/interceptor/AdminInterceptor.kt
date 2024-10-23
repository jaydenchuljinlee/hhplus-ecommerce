package com.hhplus.ecommerce.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AdminInterceptor: HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val userId = request.getHeader("X-Admin-Id")
        if (userId == null || userId.isEmpty()) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false
        }
        return true
    }
}