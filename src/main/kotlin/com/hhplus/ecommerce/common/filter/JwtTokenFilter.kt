package com.hhplus.ecommerce.common.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hhplus.ecommerce.common.dto.AuthContext
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.common.util.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtTokenProvider.resolveToken(request)

            // 토큰이 유효한지 검증
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                // JWT 토큰이 없으면 401 응답을 직접 처리
                response.status = HttpStatus.UNAUTHORIZED.value()
                response.contentType = "application/json"
                val errorResponse = CustomErrorResponse.unAuthorized("인증에 실패했습니다.")
                response.writer.write(jacksonObjectMapper().writeValueAsString(errorResponse))
                return
            }

            val jwtData = jwtTokenProvider.parseToken(token)
            AuthContext.setAuthData(jwtData)
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            // 모든 예외를 하나로 처리
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = "application/json"
            val errorResponse = CustomErrorResponse.unAuthorized("인증에 실패했습니다.")
            response.writer.write(jacksonObjectMapper().writeValueAsString(errorResponse))
        } finally {
            AuthContext.clear()
        }
    }

}