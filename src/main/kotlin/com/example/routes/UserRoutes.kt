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

fun Route.userRoutes() {
    val repository = UserRepository()

    route("/api/users") {
        get {
            val users = repository.findAll()
            call.respond(users)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.let { ObjectId(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val user = repository.findById(id)
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
            data class LoginRequest(val email: String, val password: String)
            data class LoginResponse(val token: String, val user: User)
            
            val loginRequest = call.receive<LoginRequest>()
            
            val user = repository.findByEmail(loginRequest.email)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            
            if (!BCrypt.checkpw(loginRequest.password, user.passwordHash)) {
                return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
            
            val token = JWTConfig.makeToken(user.id.toString())
            call.respond(LoginResponse(token, user.copy(passwordHash = "")))
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