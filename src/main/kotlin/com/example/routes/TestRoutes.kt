package com.example.routes

import com.example.database.MongoDB
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.testRoutes() {
    route("/api/test") {
        get("/db-status") {
            try {
                // Test connection first
                MongoDB.testConnection()

                // Get collection names
                val collectionNames = MongoDB.database.listCollectionNames().toList()
                
                // Get document counts
                val collections = mapOf(
                    "birds" to MongoDB.birds.countDocuments(),
                    "observations" to MongoDB.observations.countDocuments(),
                    "users" to MongoDB.users.countDocuments()
                )
                
                call.respond(hashMapOf(
                    "status" to "connected",
                    "collections" to collections,
                    "allCollections" to collectionNames
                ))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    hashMapOf(
                        "error" to e.message,
                        "status" to "disconnected"
                    )
                )
            }
        }

        get("/collections") {
            try {
                val birds = MongoDB.birds.find().toList()
                val observations = MongoDB.observations.find().toList()
                val users = MongoDB.users.find().toList()

                call.respond(hashMapOf(
                    "birds" to birds,
                    "observations" to observations,
                    "users" to users.map { it.copy(passwordHash = "***") }
                ))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    hashMapOf("error" to e.message)
                )
            }
        }

        get("/birds") {
            try {
                val birds = MongoDB.birds.find().toList()
                call.respond(birds)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    hashMapOf("error" to e.message)
                )
            }
        }
    }
} 