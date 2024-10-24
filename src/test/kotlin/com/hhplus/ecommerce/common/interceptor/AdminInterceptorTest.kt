package com.hhplus.ecommerce.common.interceptor

import com.hhplus.ecommerce.common.util.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class AdminInterceptorTest {
    val USER_ID = 1L
    val EMAIL = "ironjin@gmail.com"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var validToken: String

    @BeforeEach
    fun setUp() {
        // 유효한 JWT 토큰 생성
        validToken = jwtTokenProvider.createToken(USER_ID, EMAIL)
    }

    @DisplayName("Admin Header가 없는 요청은 토큰을 가지고 있어도 UN_AUTHORIZED 된다.")
    @Test
    fun adminHeaderTest() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/admin")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $validToken"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

}