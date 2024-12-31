package com.example.database

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoDB {
    private val mongoUrl = System.getenv("MONGODB_URL") ?: "mongodb://localhost:27017"
    private val client = KMongo.createClient(mongoUrl).coroutine
    val database = client.getDatabase("bird_watching_db")

    // Collections
    val birds = database.getCollection<com.example.models.Bird>("birds")
    val observations = database.getCollection<com.example.models.BirdObservation>("observations")
    val users = database.getCollection<com.example.models.User>("users")

    // Test connection
    suspend fun testConnection() {
        try {
            database.listCollectionNames().toList()
        } catch (e: Exception) {
            throw Exception("MongoDB connection failed: ${e.message}")
        }
    }
} 