package com.hhplus.ecommerce.common.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun before() {
        jwtTokenProvider = JwtTokenProvider("Ez8RZUiy088e15OapWMfWWMe_kNQglk6GQx3cxAHjhE")
    }

    @DisplayName("JWT 토큰을 생성한다.")
    @Test
    fun generateToken() {
        val userName = "ironjin"
        val roles = listOf("USER")

        val token = jwtTokenProvider.createToken(userName, roles)

        println(token)
    }

    @DisplayName("JWT 토큰에서 사용자 정보를 조회한다.")
    @Test
    fun getUserInfoFromJWT() {
        val userName = "ironjin"
        val roles = listOf("USER")

        val token = jwtTokenProvider.createToken(userName, roles)

        val userInfo = jwtTokenProvider.getUsername(token)

        assertEquals(userName, userInfo)
    }
}