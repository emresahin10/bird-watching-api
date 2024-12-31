package com.example.repositories

import com.example.database.MongoDB
import com.example.models.Bird
import org.bson.types.ObjectId
import org.litote.kmongo.*

class BirdRepository {
    private val collection = MongoDB.birds

    suspend fun findAll(): List<Bird> = collection.find().toList()

    suspend fun findById(id: ObjectId): Bird? = collection.findOneById(id)

    suspend fun findByName(name: String): List<Bird> =
        collection.find(Bird::name regex "(?i).*$name.*").toList()

    suspend fun create(bird: Bird): Bird {
        collection.insertOne(bird)
        return bird
    }

    suspend fun update(id: ObjectId, bird: Bird): Boolean {
        val update = set(
            Bird::name setTo bird.name,
            Bird::scientificName setTo bird.scientificName,
            Bird::habitat setTo bird.habitat,
            Bird::photoUrl setTo bird.photoUrl,
            Bird::description setTo bird.description,
            Bird::updatedAt setTo System.currentTimeMillis()
        )
        return collection.updateOneById(id, update).modifiedCount == 1L
    }

    suspend fun delete(id: ObjectId): Boolean =
        collection.deleteOneById(id).deletedCount == 1L
} 