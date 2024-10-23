package com.hhplus.ecommerce.common.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hhplus.ecommerce.common.dto.AuthContext
import com.hhplus.ecommerce.common.dto.JwtData
import com.hhplus.ecommerce.common.util.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
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
                sendErrorResponse(response, "Invalid JWT token")
                return
            }

            val jwtData = jwtTokenProvider.parseToken(token)
            AuthContext.setAuthData(jwtData)
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            // 모든 예외를 하나로 처리
            sendErrorResponse(response, "Invalid or expired JWT token")
            return
        } finally {
            AuthContext.clear()
        }
    }


    // 오류 응답을 JSON 형식으로 전송
    private fun sendErrorResponse(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val errorResponse = mapOf("error" to message)
        val mapper = jacksonObjectMapper()
        response.writer.write(mapper.writeValueAsString(errorResponse))
    }
}