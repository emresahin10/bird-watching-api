package com.example.routes

import com.example.models.Bird
import com.example.repositories.BirdRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.birdRoutes() {
    val repository = BirdRepository()

    route("/api/birds") {
        // Public endpoints
        get {
            val name = call.parameters["name"]
            val birds = if (name != null) {
                repository.findByName(name)
            } else {
                repository.findAll()
            }
            call.respond(birds)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.let { ObjectId(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            
            val bird = repository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Bird not found")
            
            call.respond(bird)
        }

        // Protected endpoints
        authenticate("auth-jwt") {
            post {
                val bird = call.receive<Bird>()
                val created = repository.create(bird)
                call.respond(HttpStatusCode.Created, created)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.let { ObjectId(it) }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                
                val bird = call.receive<Bird>()
                val updated = repository.update(id, bird)
                
                if (updated) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Bird not found")
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.let { ObjectId(it) }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                
                val deleted = repository.delete(id)
                
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Bird not found")
                }
            }
        }
    }
} 