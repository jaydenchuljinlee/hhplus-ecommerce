package com.hhplus.ecommerce.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class MdcFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uuid = UUID.randomUUID()
        MDC.put("Request ID", uuid.toString())
        filterChain.doFilter(request, response)
        MDC.clear()
    }
}