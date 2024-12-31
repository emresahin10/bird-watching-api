package com.example.routes

import com.example.models.BirdObservation
import com.example.repositories.BirdObservationRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.observationRoutes() {
    val repository = BirdObservationRepository()

    route("/api/observations") {
        authenticate("auth-jwt") {
            get {
                val userId = call.parameters["userId"]?.let { ObjectId(it) }
                val birdId = call.parameters["birdId"]?.let { ObjectId(it) }

                val observations = when {
                    userId != null -> repository.findByUserId(userId)
                    birdId != null -> repository.findByBirdId(birdId)
                    else -> repository.findAll()
                }
                call.respond(observations)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.let { ObjectId(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                
                val observation = repository.findById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Observation not found")
                
                call.respond(observation)
            }

            post {
                val observation = call.receive<BirdObservation>()
                val created = repository.create(observation)
                call.respond(HttpStatusCode.Created, created)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.let { ObjectId(it) }
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                
                val observation = call.receive<BirdObservation>()
                val updated = repository.update(id, observation)
                
                if (updated) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Observation not found")
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.let { ObjectId(it) }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                
                val deleted = repository.delete(id)
                
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Observation not found")
                }
            }
        }
    }
} 