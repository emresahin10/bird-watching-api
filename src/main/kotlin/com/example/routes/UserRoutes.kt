package com.example.routes

import com.example.models.User
import com.example.repositories.UserRepository
import com.example.plugins.JWTConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt
import kotlinx.serialization.Serializable

fun Route.userRoutes() {
    val repository = UserRepository()

    route("/api/users") {
        get {
            val users = repository.findAll()
            call.respond(users)
        }

        get("/{id}") {
            val userId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val user = repository.findByUserId(userId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")
            
            call.respond(user)
        }

        post("/register") {
            val user = call.receive<User>()
            
            // Check if email already exists
            repository.findByEmail(user.email)?.let {
                return@post call.respond(HttpStatusCode.Conflict, "Email already exists")
            }

            // Hash password
            val hashedPassword = BCrypt.hashpw(user.passwordHash, BCrypt.gensalt())
            val userToCreate = user.copy(passwordHash = hashedPassword)
            
            val created = repository.create(userToCreate)
            call.respond(HttpStatusCode.Created, created)
        }

        post("/login") {
            @Serializable
            data class LoginRequest(val email: String, val password: String)
            @Serializable
            data class LoginResponse(
                val accessToken: String,
                val refreshToken: String,
                val user: User
            )
            
            val loginRequest = call.receive<LoginRequest>()
            
            val user = repository.findByEmail(loginRequest.email)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            
            if (!BCrypt.checkpw(loginRequest.password, user.passwordHash)) {
                return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
            
            val accessToken = JWTConfig.makeAccessToken(user.userId)
            val refreshToken = JWTConfig.makeRefreshToken(user.userId)
            val refreshTokenExpiresAt = System.currentTimeMillis() + 3600000 // 30 days

            // Update user with refresh token
            repository.updateRefreshToken(user.id, refreshToken, refreshTokenExpiresAt)
            
            call.respond(LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = user.copy(passwordHash = "", refreshToken = null, refreshTokenExpiresAt = null)
            ))
        }

        post("/refresh-token") {
            @Serializable
            data class RefreshTokenRequest(val refreshToken: String)
            @Serializable
            data class RefreshTokenResponse(val accessToken: String)

            val request = call.receive<RefreshTokenRequest>()
            
            // Find user by refresh token
            val user = repository.findByRefreshToken(request.refreshToken)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")

            // Check if refresh token is expired
            if (user.refreshTokenExpiresAt != null && user.refreshTokenExpiresAt < System.currentTimeMillis()) {
                // Clear expired refresh token
                repository.updateRefreshToken(user.id, null, null)
                return@post call.respond(HttpStatusCode.Unauthorized, "Refresh token expired")
            }

            // Generate new access token
            val newAccessToken = JWTConfig.makeAccessToken(user.userId)
            call.respond(RefreshTokenResponse(newAccessToken))
        }

        post("/logout") {
            data class LogoutRequest(val refreshToken: String)

            val request = call.receive<LogoutRequest>()
            
            // Find user by refresh token
            val user = repository.findByRefreshToken(request.refreshToken)
                ?: return@post call.respond(HttpStatusCode.OK)

            // Clear refresh token
            repository.updateRefreshToken(user.id, null, null)
            call.respond(HttpStatusCode.OK)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.let { ObjectId(it) }
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val user = call.receive<User>()
            val updated = repository.update(id, user)
            
            if (updated) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.let { ObjectId(it) }
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val deleted = repository.delete(id)
            
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }
    }
} 