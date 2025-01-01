package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

fun Application.configureSecurity() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                throw Exception("Token is not valid or has expired")
            }
        }
    }

    // Initialize JWTConfig
    JWTConfig.apply {
        secret = jwtSecret
        issuer = jwtIssuer
        audience = jwtAudience
    }
}

object JWTConfig {
    lateinit var secret: String
    lateinit var issuer: String
    lateinit var audience: String
    private const val accessTokenValidityInMs = 3600000 // 1 hour
    private const val refreshTokenValidityInMs = 2592000000 // 30 days

    fun makeAccessToken(userId: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + accessTokenValidityInMs))
        .sign(Algorithm.HMAC256(secret))

    fun makeRefreshToken(userId: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenValidityInMs))
        .sign(Algorithm.HMAC256(secret))

    fun verifyToken(token: String): DecodedJWT? {
        return try {
            JWT.require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
                .verify(token)
        } catch (e: Exception) {
            null
        }
    }
} 