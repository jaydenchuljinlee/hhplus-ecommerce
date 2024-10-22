package com.hhplus.ecommerce.common.util

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenProvider(
    @Value("\${jwt.secretKey}")
    private var secretKey: String
) {
    init {
        // 비밀키를 Base64로 인코딩
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    // JWT 토큰 생성
    fun createToken(username: String, roles: List<String>): String {
        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = roles

        val now = Date()
        val validity = Date(now.time + 3600000) // 1시간 후 만료

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getSigningKey(): SecretKey {
        val keyByte = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyByte)
    }

    // JWT 토큰에서 사용자 정보 추출
    fun getUsername(token: String): String {
        val key: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey))
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    // JWT 토큰 유효성 확인
    fun validateToken(token: String): Boolean {
        val key: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey))
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)

            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}