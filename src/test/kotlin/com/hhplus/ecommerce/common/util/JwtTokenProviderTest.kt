package com.hhplus.ecommerce.common.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JwtTokenProviderTest {
    val USER_ID = 1L
    val EMAIL = "ironjin@gmail.com"
    val ROLE_USER = "ROLE_USER"

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun before() {
        jwtTokenProvider = JwtTokenProvider("Ez8RZUiy088e15OapWMfWWMe_kNQglk6GQx3cxAHjhE")
    }

    @DisplayName("JWT 토큰을 생성한다.")
    @Test
    fun generateToken() {
        val token = jwtTokenProvider.createToken(USER_ID, EMAIL, ROLE_USER)

        println(token)
    }

    @DisplayName("JWT 토큰에서 사용자 정보를 조회한다.")
    @Test
    fun getUserInfoFromJWT() {

        val token = jwtTokenProvider.createToken(USER_ID, EMAIL, ROLE_USER)

        val userInfo = jwtTokenProvider.parseToken(token)

        assertEquals(USER_ID, userInfo.userId)
        assertEquals(EMAIL, userInfo.email)
        assertEquals(ROLE_USER, userInfo.role)
    }
}