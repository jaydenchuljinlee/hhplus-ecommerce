package com.hhplus.ecommerce.common.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hhplus.ecommerce.common.util.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.util.net.openssl.ciphers.Authentication
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        try {
            // 토큰이 유효한지 검증
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                sendErrorResponse(response, "Invalid JWT token")
                return
            }
        } catch (e: Exception) {
            // 모든 예외를 하나로 처리
            sendErrorResponse(response, "Invalid or expired JWT token")
            return
        }

        filterChain.doFilter(request, response)
    }

    // 헤더에서 토큰 추출
    private fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
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