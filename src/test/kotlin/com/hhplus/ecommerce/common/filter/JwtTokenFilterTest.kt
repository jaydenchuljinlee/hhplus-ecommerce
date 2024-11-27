package com.hhplus.ecommerce.common.filter

import com.hhplus.ecommerce.common.util.JwtTokenProvider
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.Test

//@SpringBootTest
//@AutoConfigureMockMvc
class JwtTokenFilterTest {
    val USER_ID = 1L
    val EMAIL = "ironjin@gmail.com"
    val ROLE = "ROLE_USER"

    // @Autowired
    private lateinit var mockMvc: MockMvc

    // @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var validToken: String
    private lateinit var invalidToken: String

    // @BeforeEach
    fun setUp() {
        // 유효한 JWT 토큰 생성
        validToken = jwtTokenProvider.createToken(USER_ID, EMAIL, ROLE)

        // 유효하지 않은 토큰 생성
        invalidToken = "invalid.jwt.token"
    }

//    @DisplayName("유효하지 않은 JWT 토큰을 사용한 요청은 401 Unauthorized를 반환해야 한다")
//    @Test
    fun testInvalidToken() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/some-secured-endpoint")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $invalidToken")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpServletResponse.SC_UNAUTHORIZED))
    }

//    @DisplayName("토큰 없이 요청하면 401 Unauthorized를 반환해야 한다")
//    @Test
    fun testNoToken() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/some-secured-endpoint")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpServletResponse.SC_UNAUTHORIZED))
    }
}