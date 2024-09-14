package org.heatpump.backbone.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {

    private val secretKey: Key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))

    private val accessTokenValidity: Long = 60 * 60 * 1000 // 1 hour for access token
    private val refreshTokenValidity: Long = 7 * accessTokenValidity // 30 days for refresh token

    fun generateAccessToken(username: String): String {
        return generateToken(username, accessTokenValidity, "access")
    }

    fun generateRefreshToken(username: String): String {
        return generateToken(username, refreshTokenValidity, "refresh")
    }

    private fun generateToken(username: String, validityPeriod: Long, tokenType: String): String {
        val claims: Claims = Jwts.claims().setSubject(username)
        claims["token_type"] = tokenType

        val now = Date()
        val expiryDate = Date(now.time + validityPeriod)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaimsFromToken(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return getClaimsFromToken(token).subject
    }

    fun getTokenType(token: String): String {
        return getClaimsFromToken(token)["token_type"] as String
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}